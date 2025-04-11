package com.sharedpay.shared.payment.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDto {
    private Long parentId;
    private Long studentId;
    private BigDecimal paymentAmount;

}
