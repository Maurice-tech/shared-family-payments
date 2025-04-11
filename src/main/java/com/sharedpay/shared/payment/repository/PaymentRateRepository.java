package com.sharedpay.shared.payment.repository;
import com.sharedpay.shared.payment.entity.PaymentRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRateRepository  extends JpaRepository<PaymentRate, Long> {
}
