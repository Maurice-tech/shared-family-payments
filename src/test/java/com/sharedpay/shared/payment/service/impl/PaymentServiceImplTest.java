package com.sharedpay.shared.payment.service.impl;
import com.sharedpay.shared.payment.entity.Parent;
import com.sharedpay.shared.payment.entity.ParentBalanceAudit;
import com.sharedpay.shared.payment.exception.ResourceNotFoundException;
import com.sharedpay.shared.payment.exception.UnAuthorizedException;
import com.sharedpay.shared.payment.payload.PaymentRequestDto;
import com.sharedpay.shared.payment.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class PaymentServiceImplTest {

    @Mock
    private ParentRepository parentRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private PaymentRateRepository paymentRateRepository;

    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;

    @Mock
    private ParentBalanceAuditRepository auditRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock authentication
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @BeforeEach
    void setUpSecurityContext() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void processPayment_shouldThrowUnauthorized_whenAmountIsZeroOrLess() {
        PaymentRequestDto request = new PaymentRequestDto();
        request.setPaymentAmount(BigDecimal.ZERO);
        request.setParentId(1L);
        request.setStudentId(1L);

        UnAuthorizedException exception = assertThrows(UnAuthorizedException.class, () -> {
            paymentService.processPayment(request);
        });

        assertEquals("Payment amount must be greater than zero", exception.getMessage());
    }

    @Test
    void processPayment_shouldThrowResourceNotFound_whenParentNotFound() {
        PaymentRequestDto request = new PaymentRequestDto();
        request.setPaymentAmount(BigDecimal.valueOf(100));
        request.setParentId(1L);
        request.setStudentId(1L);

        when(parentRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            paymentService.processPayment(request);
        });

        assertEquals("Parent not found", exception.getMessage());
    }

    @Test
    void processPayment_shouldThrowResourceNotFound_whenStudentNotFound() {
        PaymentRequestDto request = new PaymentRequestDto();
        request.setPaymentAmount(BigDecimal.valueOf(100));
        request.setParentId(1L);
        request.setStudentId(1L);

        Parent parent = new Parent();
        when(parentRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            paymentService.processPayment(request);
        });

        assertEquals("Student not found", exception.getMessage());
    }

    @Test
    void addAmountToParent_shouldThrowUnauthorized_whenAmountIsZeroOrNegative() {
        UnAuthorizedException exception = assertThrows(UnAuthorizedException.class, () -> {
            paymentService.addAmountToParent(1L, BigDecimal.ZERO);
        });

        assertEquals("Amount must be greater than zero", exception.getMessage());
    }

    @Test
    void addAmountToParent_shouldThrowResourceNotFound_whenParentNotFound() {
        when(parentRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            paymentService.addAmountToParent(1L, BigDecimal.valueOf(100));
        });

        assertEquals("Parent not found", exception.getMessage());
    }

    @Test
    void addAmountToParent_shouldUpdateBalanceSuccessfully() {

        Parent parent = new Parent();
        parent.setBalance(BigDecimal.valueOf(200));

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testUser");
        when(auth.isAuthenticated()).thenReturn(true);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        when(parentRepository.findById(1L)).thenReturn(Optional.of(parent));


        paymentService.addAmountToParent(1L, BigDecimal.valueOf(100));


        assertEquals(BigDecimal.valueOf(300), parent.getBalance());
        verify(parentRepository, times(1)).save(parent);
        verify(auditRepository, times(1)).save(any(ParentBalanceAudit.class));
    }


}
