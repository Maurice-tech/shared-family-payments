package com.sharedpay.shared.payment.entity;

import jakarta.persistence.Entity;
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
public class ParentBalanceAudit extends BaseEntity{
    private Long parentId;

    private BigDecimal amountChanged;

    private BigDecimal previousBalance;

    private BigDecimal newBalance;

    private String performedBy;

    private String action;
}
