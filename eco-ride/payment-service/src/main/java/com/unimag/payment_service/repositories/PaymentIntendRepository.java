package com.unimag.payment_service.repositories;

import com.unimag.payment_service.entities.PaymentIntent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface PaymentIntendRepository extends R2dbcRepository<PaymentIntent,String> {
    Mono<PaymentIntent> findByReservationId(String s);
}
