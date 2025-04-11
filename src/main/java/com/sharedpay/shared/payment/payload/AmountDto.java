package com.sharedpay.shared.payment.payload;

import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;

@Data
public class AmountDto {
    private BigDecimal amount;
}
