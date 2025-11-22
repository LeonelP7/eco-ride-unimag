package com.unimag.payment_service.repositories;

import com.unimag.payment_service.entities.PaymentIntent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentIntendRepository extends JpaRepository<PaymentIntent, String> {
}
