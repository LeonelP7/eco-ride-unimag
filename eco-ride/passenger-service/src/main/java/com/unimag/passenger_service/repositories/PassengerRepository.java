package com.unimag.passenger_service.repositories;

import com.unimag.passenger_service.entities.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, String> {

    Optional<Passenger> findByKeycloakSub(String keycloakSub);

    Optional<Passenger> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByKeycloakSub(String keycloakSub);
}