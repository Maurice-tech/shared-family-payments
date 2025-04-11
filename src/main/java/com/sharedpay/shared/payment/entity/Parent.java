package com.sharedpay.shared.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Parent extends BaseEntity {

    private String name;
    private BigDecimal balance;
    @ManyToMany(mappedBy = "parents")
    private Set<Student> students = new HashSet<>();
}
