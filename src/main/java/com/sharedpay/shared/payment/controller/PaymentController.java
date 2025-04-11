package com.sharedpay.shared.payment.controller;
import com.sharedpay.shared.payment.payload.AmountDto;
import com.sharedpay.shared.payment.payload.PaymentRequestDto;
import com.sharedpay.shared.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;


    @PostMapping
    public ResponseEntity<String> makePayment(@RequestBody PaymentRequestDto request) {
        paymentService.processPayment(request);
        return ResponseEntity.ok("Payment successful");
    }

    @PostMapping("/credit-parent/{parentId}")
    public ResponseEntity<String> creditParentBalance(
            @PathVariable Long parentId,
            @RequestBody AmountDto amountDto
    ) {
        try {
            paymentService.addAmountToParent(parentId, amountDto.getAmount());
            return ResponseEntity.ok("Parent balance updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating parent's balance: " + e.getMessage());
        }
    }
}

