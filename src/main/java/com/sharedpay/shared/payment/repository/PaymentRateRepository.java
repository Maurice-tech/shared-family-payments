package com.sharedpay.shared.payment.repository;
import com.sharedpay.shared.payment.entity.PaymentRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRateRepository  extends JpaRepository<PaymentRate, Long> {
}
