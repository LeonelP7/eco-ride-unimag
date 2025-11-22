package com.unimag.trip_service.services;

import com.unimag.trip_service.dtos.trip.CreateTripDTO;
import com.unimag.trip_service.dtos.trip.ResponseTripDTO;
import com.unimag.trip_service.entities.Trip;
import com.unimag.trip_service.mappers.TripMapper;
import com.unimag.trip_service.respositories.TripRepository;
import com.unimag.trip_service.services.publisher.EventPublisherService;
import com.unimag.trip_service.util.TripSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final TripMapper tripMapper;

    @Override
    public ResponseTripDTO saveTrip(CreateTripDTO createTripDTO, String driverId) {
        // falta añadir en el controller @AuthenticationPrincipal(expression = "id") Long driverId una vez se añada el security

        Trip createdTrip = tripMapper.createDTOToTrip(createTripDTO);

        return tripMapper.tripToResponse(tripRepository.save(createdTrip), driverId);
    }

    @Override
    public List<ResponseTripDTO> getTrips() {
        return tripRepository.findAll()
                .stream()
                .map(t -> tripMapper.tripToResponse(t,t.getDriverId()))
                .toList();
    }

    @Override
    public List<ResponseTripDTO> findByFilters(String origin, String destination, LocalDateTime from, LocalDateTime to) {
        Specification<Trip> spec = Specification.allOf(
                TripSpecifications.hasOrigin(origin),
                TripSpecifications.hasDestination(destination),
                TripSpecifications.startsInRange(from, to)
        );

        return tripRepository.findAll(spec)
                .stream()
                .map(t -> tripMapper.tripToResponse(t, t.getDriverId()))
                .toList();
    }
}
