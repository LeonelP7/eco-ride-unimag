package com.unimag.trip_service.services;

import com.unimag.trip_service.dtos.trip.CreateTripDTO;
import com.unimag.trip_service.dtos.trip.ResponseTripDTO;
import com.unimag.trip_service.entities.Trip;
import com.unimag.trip_service.mappers.TripMapper;
import com.unimag.trip_service.respositories.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final TripMapper tripMapper;

    @Override
    public Mono<ResponseTripDTO> saveTrip(CreateTripDTO createTripDTO, String driverId) {
        // falta añadir en el controller @AuthenticationPrincipal(expression = "id") Long driverId una vez se añada el security

        Trip newTrip = tripMapper.createDTOToTrip(createTripDTO);
        newTrip.setId(UUID.randomUUID().toString());

        return tripRepository
                .save(newTrip)
                .map(savedTrip ->
                        tripMapper.tripToResponse(savedTrip, driverId)
                );
    }

    @Override
    public Flux<ResponseTripDTO> getTrips() {
        return tripRepository.findAll()
                .map(t -> tripMapper.tripToResponse(t,t.getDriverId()));
    }

    @Override
    public Flux<ResponseTripDTO> findByFilters(String origin, String destination, LocalDateTime from, LocalDateTime to) {
        return tripRepository.findByFilters(
                        origin != null && !origin.isBlank() ? origin : null,
                        destination != null && !destination.isBlank() ? destination : null,
                        from,
                        to
                )
                .map(t -> tripMapper.tripToResponse(t, t.getDriverId()));
    }
}
