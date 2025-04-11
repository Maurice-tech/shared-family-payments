package com.sharedpay.shared.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class PaymentTransaction extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false)
    private Parent parent;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private BigDecimal paymentAmount;

    @Column(nullable = false)
    private String transactionType;

    @Column(nullable = false)
    private BigDecimal feeApplied;
}
