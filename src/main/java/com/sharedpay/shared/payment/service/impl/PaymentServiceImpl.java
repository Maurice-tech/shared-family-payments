package com.sharedpay.shared.payment.service.impl;
import com.sharedpay.shared.payment.entity.*;
import com.sharedpay.shared.payment.exception.ResourceNotFoundException;
import com.sharedpay.shared.payment.exception.UnAuthorizedException;
import com.sharedpay.shared.payment.payload.PaymentRequestDto;
import com.sharedpay.shared.payment.repository.*;
import com.sharedpay.shared.payment.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private final PaymentRateRepository paymentRateRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final ParentBalanceAuditRepository auditRepository;

    @Override
    @Transactional
    public void processPayment(PaymentRequestDto request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnAuthorizedException("User is not authenticated");
        }

        if (request.getPaymentAmount() == null || request.getPaymentAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new UnAuthorizedException("Payment amount must be greater than zero");
        }

        Parent payer = parentRepository.findById(request.getParentId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found"));

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        PaymentRate paymentRate = paymentRateRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException("Payment settings not found"));

        BigDecimal dynamicRate = paymentRate.getDynamicRate();
        BigDecimal adjustedAmount = request.getPaymentAmount().multiply(BigDecimal.ONE.add(dynamicRate));
        String transactionType = "Unique";

        if (student.getParents().size() == 1) {
            BigDecimal payerBalance = safeBalance(payer.getBalance());
            if (payerBalance.compareTo(adjustedAmount) < 0) {
                throw new UnAuthorizedException("Insufficient balance to make this payment.");
            }
            payer.setBalance(payerBalance.subtract(adjustedAmount));
            parentRepository.save(payer);
        } else {
            transactionType = "Shared";
            int parentCount = student.getParents().size();
            BigDecimal share = adjustedAmount.divide(BigDecimal.valueOf(parentCount), RoundingMode.HALF_UP);


            for (Parent parent : student.getParents()) {
                BigDecimal parentBalance = safeBalance(parent.getBalance());
                if (parentBalance.compareTo(share) < 0) {
                    throw new UnAuthorizedException("Parent " + parent.getName() + " does not have enough balance for shared payment.");
                }
            }

            for (Parent parent : student.getParents()) {
                BigDecimal parentBalance = safeBalance(parent.getBalance());
                parent.setBalance(parentBalance.subtract(share));
                parentRepository.save(parent);
            }
        }

        student.setBalance(safeBalance(student.getBalance()).add(adjustedAmount));
        studentRepository.save(student);

        recordPaymentTransaction(payer, student, adjustedAmount, transactionType, dynamicRate);
    }

    private void recordPaymentTransaction(Parent payer, Student student, BigDecimal paymentAmount, String transactionType, BigDecimal feeApplied) {
        PaymentTransaction paymentTransaction = new PaymentTransaction();
        paymentTransaction.setParent(payer);
        paymentTransaction.setStudent(student);
        paymentTransaction.setPaymentAmount(paymentAmount);
        paymentTransaction.setTimestamp(LocalDateTime.now());
        paymentTransaction.setTransactionType(transactionType);
        paymentTransaction.setFeeApplied(feeApplied);

        paymentTransactionRepository.save(paymentTransaction);
    }

    private BigDecimal safeBalance(BigDecimal balance) {
        return balance != null ? balance : BigDecimal.ZERO;
    }


    @Override
    public void addAmountToParent(Long parentId, BigDecimal amountToAdd) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnAuthorizedException("User is not authenticated");
        }

        if (amountToAdd == null || amountToAdd.compareTo(BigDecimal.ZERO) <= 0) {
            throw new UnAuthorizedException("Amount must be greater than zero");
        }

        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found"));

        BigDecimal currentBalance = safeBalance(parent.getBalance());
        BigDecimal newBalance = currentBalance.add(amountToAdd);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new UnAuthorizedException("Parent balance cannot go below zero");
        }

        parent.setBalance(newBalance);
        parentRepository.save(parent);


        ParentBalanceAudit audit = new ParentBalanceAudit();
        audit.setParentId(parentId);
        audit.setAmountChanged(amountToAdd);
        audit.setPreviousBalance(currentBalance);
        audit.setNewBalance(newBalance);
        audit.setPerformedBy(authentication.getName());
        audit.setTimestamp(LocalDateTime.now());
        audit.setAction("ADD_FUNDS");

        auditRepository.save(audit);
    }

}
