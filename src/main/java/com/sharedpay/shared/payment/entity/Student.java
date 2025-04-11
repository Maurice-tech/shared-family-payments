package com.sharedpay.shared.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

import java.math.BigDecimal;
import java.util.HashSet;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Student extends BaseEntity {

    private String name;
    private BigDecimal balance;
    @ManyToMany
    @JoinTable(
            name = "parent_student",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "parent_id")
    )
    private Set<Parent> parents = new HashSet<>();
}
