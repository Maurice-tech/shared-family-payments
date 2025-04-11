package com.sharedpay.shared.payment.repository;

import com.sharedpay.shared.payment.entity.ParentBalanceAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParentBalanceAuditRepository extends JpaRepository<ParentBalanceAudit, Long> {
}
