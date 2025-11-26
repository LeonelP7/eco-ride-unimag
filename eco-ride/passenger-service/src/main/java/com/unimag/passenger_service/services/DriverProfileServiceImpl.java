package com.unimag.passenger_service.services;

import com.unimag.passenger_service.dtos.driver.CreateDriverProfileDTO;
import com.unimag.passenger_service.dtos.driver.ResponseDriverProfileDTO;
import com.unimag.passenger_service.dtos.driver.UpdateDriverProfileDTO;
import com.unimag.passenger_service.entities.DriverProfile;
import com.unimag.passenger_service.enums.VerificationStatus;
import com.unimag.passenger_service.exceptions.conflict.DriverProfileAlreadyExistsException;
import com.unimag.passenger_service.exceptions.notfound.DriverProfileNotFoundException;
import com.unimag.passenger_service.exceptions.notfound.PassengerNotFoundException;
import com.unimag.passenger_service.mappers.DriverProfileMapper;
import com.unimag.passenger_service.repositories.DriverProfileRepository;
import com.unimag.passenger_service.repositories.PassengerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
@Service
@RequiredArgsConstructor
@Slf4j
public class DriverProfileServiceImpl implements DriverProfileService {

    private final DriverProfileRepository driverProfileRepository;
    private final PassengerRepository passengerRepository;
    private final DriverProfileMapper driverProfileMapper;

    @Override
    public Mono<ResponseDriverProfileDTO> createDriverProfile(CreateDriverProfileDTO dto) {
        return passengerRepository.existsById(dto.passengerId())
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new PassengerNotFoundException(
                                "Passenger not found with id: " + dto.passengerId()));
                    }

                    return driverProfileRepository.existsByPassengerId(dto.passengerId())
                            .flatMap(profileExists -> {
                                if (profileExists) {
                                    return Mono.error(new DriverProfileAlreadyExistsException(
                                            "Driver profile already exists for passenger: " + dto.passengerId()));
                                }

                                DriverProfile driverProfile = driverProfileMapper.toEntity(dto);
                                driverProfile.setId(UUID.randomUUID().toString());
                                driverProfile.setVerificationStatus(VerificationStatus.PENDING);

                                return driverProfileRepository.save(driverProfile)
                                        .map(driverProfileMapper::toResponseDTO)
                                        .doOnSuccess(d -> log.info("Driver profile created: {}", d.id()));
                            });
                });
    }

    @Override
    public Mono<ResponseDriverProfileDTO> getDriverProfileById(String id) {
        return driverProfileRepository.findById(id)
                .map(driverProfileMapper::toResponseDTO)
                .switchIfEmpty(Mono.error(new DriverProfileNotFoundException(
                        "Driver profile not found with id: " + id)));
    }

    @Override
    public Mono<ResponseDriverProfileDTO> getDriverProfileByPassengerId(String passengerId) {
        return driverProfileRepository.findByPassengerId(passengerId)
                .map(driverProfileMapper::toResponseDTO)
                .switchIfEmpty(Mono.error(new DriverProfileNotFoundException(
                        "Driver profile not found for passenger: " + passengerId)));
    }

    @Override
    public Flux<ResponseDriverProfileDTO> getAllDriverProfiles() {
        return driverProfileRepository.findAll()
                .map(driverProfileMapper::toResponseDTO);
    }

    @Override
    public Mono<ResponseDriverProfileDTO> updateDriverProfile(String id, UpdateDriverProfileDTO dto) {
        return driverProfileRepository.findById(id)
                .switchIfEmpty(Mono.error(new DriverProfileNotFoundException(
                        "Driver profile not found with id: " + id)))
                .flatMap(driverProfile -> {
                    driverProfileMapper.updateEntityFromDTO(dto, driverProfile);

                    return driverProfileRepository.save(driverProfile)
                            .map(driverProfileMapper::toResponseDTO)
                            .doOnSuccess(d -> log.info("Driver profile updated: {}", d.id()));
                });
    }

    @Override
    public Mono<ResponseDriverProfileDTO> updateVerificationStatus(String id, VerificationStatus status) {
        return driverProfileRepository.findById(id)
                .switchIfEmpty(Mono.error(new DriverProfileNotFoundException(
                        "Driver profile not found with id: " + id)))
                .flatMap(driverProfile -> {
                    driverProfile.setVerificationStatus(status);

                    return driverProfileRepository.save(driverProfile)
                            .map(driverProfileMapper::toResponseDTO)
                            .doOnSuccess(d -> log.info("Driver verification status updated: {} -> {}",
                                    d.id(), status));
                });
    }

    @Override
    public Mono<Void> deleteDriverProfile(String id) {
        return driverProfileRepository.findById(id)
                .switchIfEmpty(Mono.error(new DriverProfileNotFoundException(
                        "Driver profile not found with id: " + id)))
                .flatMap(driverProfile -> driverProfileRepository.delete(driverProfile)
                        .doOnSuccess(v -> log.info("Driver profile deleted: {}", id)));
    }
}