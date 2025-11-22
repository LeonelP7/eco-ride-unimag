package com.unimag.payment_service.repositories;

import com.unimag.payment_service.entities.Charge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChargeRepository extends JpaRepository<Charge, String> {
}
