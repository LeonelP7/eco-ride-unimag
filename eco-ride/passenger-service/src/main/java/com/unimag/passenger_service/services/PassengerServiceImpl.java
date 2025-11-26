package com.unimag.passenger_service.services;

import com.unimag.passenger_service.dtos.passenger.CreatePassengerDTO;
import com.unimag.passenger_service.dtos.passenger.ResponsePassengerDTO;
import com.unimag.passenger_service.dtos.passenger.UpdatePassengerDTO;
import com.unimag.passenger_service.entities.Passenger;
import com.unimag.passenger_service.exceptions.conflict.PassengerAlreadyExistsException;
import com.unimag.passenger_service.exceptions.notfound.PassengerNotFoundException;
import com.unimag.passenger_service.mappers.PassengerMapper;
import com.unimag.passenger_service.repositories.PassengerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final PassengerMapper passengerMapper;

    @Override
    public Mono<ResponsePassengerDTO> createPassenger(CreatePassengerDTO dto) {
        // ✅ 1. Verificar si ya existe
        return passengerRepository.existsByEmail(dto.email())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new PassengerAlreadyExistsException(
                                "Passenger with email " + dto.email() + " already exists"));
                    }

                    // ✅ 2. Crear la entidad
                    Passenger passenger = passengerMapper.toEntity(dto);
                    passenger.setId(UUID.randomUUID().toString());
                    passenger.setCreatedAt(LocalDateTime.now());
                    passenger.setRatingAvg(0.0);

                    // ✅ 3. Guardar y mapear a DTO
                    return passengerRepository.save(passenger)
                            .map(passengerMapper::toResponseDTO)
                            .doOnSuccess(p -> log.info("Passenger created: {}", p.id()));
                });
    }

    @Override
    public Mono<ResponsePassengerDTO> getPassengerById(String id) {
        return passengerRepository.findById(id)
                .map(passengerMapper::toResponseDTO)
                .switchIfEmpty(Mono.error(new PassengerNotFoundException(
                        "Passenger not found with id: " + id)));
    }

    @Override
    public Mono<ResponsePassengerDTO> getPassengerByEmail(String email) {
        return passengerRepository.findByEmail(email)
                .map(passengerMapper::toResponseDTO)
                .switchIfEmpty(Mono.error(new PassengerNotFoundException(
                        "Passenger not found with email: " + email)));
    }

    @Override
    public Mono<ResponsePassengerDTO> getPassengerByKeycloakSub(String keycloakSub) {
        return passengerRepository.findByKeycloakSub(keycloakSub)
                .map(passengerMapper::toResponseDTO)
                .switchIfEmpty(Mono.error(new PassengerNotFoundException(
                        "Passenger not found with keycloakSub: " + keycloakSub)));
    }

    @Override
    public Flux<ResponsePassengerDTO> getAllPassengers() {
        return passengerRepository.findAll()
                .map(passengerMapper::toResponseDTO);
    }

    @Override
    public Mono<ResponsePassengerDTO> updatePassenger(String id, UpdatePassengerDTO dto) {
        return passengerRepository.findById(id)
                .switchIfEmpty(Mono.error(new PassengerNotFoundException(
                        "Passenger not found with id: " + id)))
                .flatMap(passenger -> {
                    // ✅ Actualizar campos
                    passengerMapper.updateEntityFromDTO(dto, passenger);

                    // ✅ Guardar y retornar
                    return passengerRepository.save(passenger)
                            .map(passengerMapper::toResponseDTO)
                            .doOnSuccess(p -> log.info("Passenger updated: {}", p.id()));
                });
    }

    @Override
    public Mono<Void> deletePassenger(String id) {
        return passengerRepository.findById(id)
                .switchIfEmpty(Mono.error(new PassengerNotFoundException(
                        "Passenger not found with id: " + id)))
                .flatMap(passenger -> passengerRepository.delete(passenger)
                        .doOnSuccess(v -> log.info("Passenger deleted: {}", id)));
    }
}