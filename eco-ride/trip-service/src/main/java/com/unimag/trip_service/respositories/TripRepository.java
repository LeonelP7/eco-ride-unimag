package com.unimag.trip_service.respositories;

import com.unimag.trip_service.entities.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TripRepository extends JpaRepository<Trip, String>, JpaSpecificationExecutor<Trip> {

}
