package com.unimag.passenger_service.services;

import com.unimag.passenger_service.dtos.rating.CreateRatingDTO;
import com.unimag.passenger_service.dtos.rating.ResponseRatingDTO;
import com.unimag.passenger_service.entities.Rating;
import com.unimag.passenger_service.exceptions.notfound.PassengerNotFoundException;
import com.unimag.passenger_service.exceptions.notfound.RatingNotFoundException;
import com.unimag.passenger_service.mappers.RatingMapper;
import com.unimag.passenger_service.repositories.PassengerRepository;
import com.unimag.passenger_service.repositories.RatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final PassengerRepository passengerRepository;
    private final RatingMapper ratingMapper;

    @Override
    public Mono<ResponseRatingDTO> createRating(CreateRatingDTO dto) {
        return passengerRepository.existsById(dto.fromPassengerId())
                .flatMap(fromExists -> {
                    if (!fromExists) {
                        return Mono.error(new PassengerNotFoundException(
                                "Passenger not found with id: " + dto.fromPassengerId()));
                    }

                    return passengerRepository.existsById(dto.toPassengerId())
                            .flatMap(toExists -> {
                                if (!toExists) {
                                    return Mono.error(new PassengerNotFoundException(
                                            "Passenger not found with id: " + dto.toPassengerId()));
                                }

                                Rating rating = ratingMapper.toEntity(dto);
                                rating.setId(UUID.randomUUID().toString());

                                return ratingRepository.save(rating)
                                        .flatMap(savedRating -> updatePassengerRating(dto.toPassengerId())
                                                .thenReturn(savedRating))
                                        .map(ratingMapper::toResponseDTO)
                                        .doOnSuccess(r -> log.info("Rating created: {}", r.id()));
                            });
                });
    }

    private Mono<Void> updatePassengerRating(String passengerId) {
        return ratingRepository.calculateAverageRatingForPassenger(passengerId)
                .flatMap(avgRating -> passengerRepository.findById(passengerId)
                        .flatMap(passenger -> {
                            passenger.setRatingAvg(avgRating != null ? avgRating : 0.0);
                            return passengerRepository.save(passenger).then();
                        }));
    }

    @Override
    public Mono<ResponseRatingDTO> getRatingById(String id) {
        return ratingRepository.findById(id)
                .map(ratingMapper::toResponseDTO)
                .switchIfEmpty(Mono.error(new RatingNotFoundException(
                        "Rating not found with id: " + id)));
    }

    @Override
    public Flux<ResponseRatingDTO> getRatingsByFromId(String fromId) {
        return ratingRepository.findByFromId(fromId)
                .map(ratingMapper::toResponseDTO);
    }

    @Override
    public Flux<ResponseRatingDTO> getRatingsByToId(String toId) {
        return ratingRepository.findByToId(toId)
                .map(ratingMapper::toResponseDTO);
    }

    @Override
    public Flux<ResponseRatingDTO> getRatingsByTripId(String tripId) {
        return ratingRepository.findByTripId(tripId)
                .map(ratingMapper::toResponseDTO);
    }

    @Override
    public Flux<ResponseRatingDTO> getAllRatings() {
        return ratingRepository.findAll()
                .map(ratingMapper::toResponseDTO);
    }
}