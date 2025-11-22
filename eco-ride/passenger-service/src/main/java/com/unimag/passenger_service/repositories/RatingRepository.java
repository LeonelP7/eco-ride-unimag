package com.unimag.passenger_service.repositories;

import com.unimag.passenger_service.entities.Passenger;
import com.unimag.passenger_service.entities.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, String> {

    List<Rating> findByToPassenger(Passenger passenger);

    List<Rating> findByFromPassenger(Passenger passenger);

    List<Rating> findByTripId(String tripId);

    @Query("SELECT AVG(r.score) FROM ratings r WHERE r.toPassenger = :passenger")
    Double calculateAverageRatingForPassenger(@Param("passenger") Passenger passenger);
}