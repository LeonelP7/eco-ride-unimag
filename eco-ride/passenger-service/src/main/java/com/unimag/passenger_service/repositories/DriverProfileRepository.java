package com.unimag.passenger_service.repositories;

import com.unimag.passenger_service.entities.DriverProfile;
import com.unimag.passenger_service.entities.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverProfileRepository extends JpaRepository<DriverProfile, String> {

    Optional<DriverProfile> findByPassenger(Passenger passenger);

    Optional<DriverProfile> findByLicenseNo(String licenseNo);

    boolean existsByLicenseNo(String licenseNo);

    boolean existsByPassenger(Passenger passenger);
}