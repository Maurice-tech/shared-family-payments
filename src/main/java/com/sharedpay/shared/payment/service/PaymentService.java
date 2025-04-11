package com.sharedpay.shared.payment.service;

import com.sharedpay.shared.payment.payload.PaymentRequestDto;

import java.math.BigDecimal;

public interface PaymentService {
    void processPayment(PaymentRequestDto request);

    void addAmountToParent(Long parentId, BigDecimal amountToAdd);
}
