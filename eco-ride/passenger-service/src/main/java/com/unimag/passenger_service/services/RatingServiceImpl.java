package com.unimag.passenger_service.services;

import com.unimag.passenger_service.dtos.rating.CreateRatingDTO;
import com.unimag.passenger_service.dtos.rating.ResponseRatingDTO;
import com.unimag.passenger_service.entities.Passenger;
import com.unimag.passenger_service.entities.Rating;
import com.unimag.passenger_service.exceptions.notfound.PassengerNotFoundException;
import com.unimag.passenger_service.exceptions.notfound.RatingNotFoundException;
import com.unimag.passenger_service.mappers.RatingMapper;
import com.unimag.passenger_service.repositories.PassengerRepository;
import com.unimag.passenger_service.repositories.RatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final PassengerRepository passengerRepository;
    private final RatingMapper ratingMapper;

    @Override
    @Transactional
    public ResponseRatingDTO createRating(CreateRatingDTO dto) {
        log.info("Creating rating for trip: {}", dto.tripId());

        // Buscar pasajeros
        Passenger fromPassenger = passengerRepository.findById(dto.fromPassengerId())
                .orElseThrow(() -> new PassengerNotFoundException("From passenger not found with ID: " + dto.fromPassengerId()));

        Passenger toPassenger = passengerRepository.findById(dto.toPassengerId())
                .orElseThrow(() -> new PassengerNotFoundException("To passenger not found with ID: " + dto.toPassengerId()));

        Rating rating = ratingMapper.toEntity(dto);
        rating.setFromPassenger(fromPassenger);
        rating.setToPassenger(toPassenger);

        Rating savedRating = ratingRepository.save(rating);

        // Actualizar rating promedio del pasajero calificado
        updatePassengerAverageRating(toPassenger);

        log.info("Rating created successfully with ID: {}", savedRating.getId());
        return ratingMapper.toResponseDTO(savedRating);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseRatingDTO getRatingById(String id) {
        log.info("Fetching rating with ID: {}", id);

        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new RatingNotFoundException("Rating not found with ID: " + id));

        return ratingMapper.toResponseDTO(rating);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseRatingDTO> getRatingsByPassengerId(String passengerId) {
        log.info("Fetching ratings for passenger ID: {}", passengerId);

        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new PassengerNotFoundException("Passenger not found with ID: " + passengerId));

        return ratingRepository.findByToPassenger(passenger).stream()
                .map(ratingMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseRatingDTO> getRatingsByTripId(String tripId) {
        log.info("Fetching ratings for trip ID: {}", tripId);

        return ratingRepository.findByTripId(tripId).stream()
                .map(ratingMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseRatingDTO> getAllRatings() {
        log.info("Fetching all ratings");

        return ratingRepository.findAll().stream()
                .map(ratingMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteRating(String id) {
        log.info("Deleting rating with ID: {}", id);

        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new RatingNotFoundException("Rating not found with ID: " + id));

        Passenger toPassenger = rating.getToPassenger();

        ratingRepository.deleteById(id);

        // Recalcular rating promedio
        updatePassengerAverageRating(toPassenger);

        log.info("Rating deleted successfully with ID: {}", id);
    }

    private void updatePassengerAverageRating(Passenger passenger) {
        Double averageRating = ratingRepository.calculateAverageRatingForPassenger(passenger);
        List<Rating> ratings = ratingRepository.findByToPassenger(passenger);

        passenger.setRatingAvg(averageRating != null ? averageRating : 0.0);
        passenger.setTotalRatings(ratings.size());

        passengerRepository.save(passenger);
        log.info("Updated average rating for passenger ID: {} to {}", passenger.getId(), passenger.getRatingAvg());
    }
}