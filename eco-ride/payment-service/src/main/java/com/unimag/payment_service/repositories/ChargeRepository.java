package com.unimag.payment_service.repositories;

import com.unimag.payment_service.entities.Charge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ChargeRepository extends R2dbcRepository<Charge, String> {
}
