package com.sharedpay.shared.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Payment extends BaseEntity {

    private BigDecimal originalAmount;
    private BigDecimal adjustedAmount;
    @ManyToOne
    private Parent paidBy;
    @ManyToOne
    private Student student;
}
