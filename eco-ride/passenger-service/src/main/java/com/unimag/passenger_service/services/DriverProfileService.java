package com.unimag.passenger_service.services;

import com.unimag.passenger_service.dtos.driver.CreateDriverProfileDTO;
import com.unimag.passenger_service.dtos.driver.ResponseDriverProfileDTO;
import com.unimag.passenger_service.dtos.driver.UpdateDriverProfileDTO;
import com.unimag.passenger_service.enums.VerificationStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DriverProfileService {

    Mono<ResponseDriverProfileDTO> createDriverProfile(CreateDriverProfileDTO dto);

    Mono<ResponseDriverProfileDTO> getDriverProfileById(String id);

    Mono<ResponseDriverProfileDTO> getDriverProfileByPassengerId(String passengerId);

    Flux<ResponseDriverProfileDTO> getAllDriverProfiles();

    Mono<ResponseDriverProfileDTO> updateDriverProfile(String id, UpdateDriverProfileDTO dto);

    Mono<ResponseDriverProfileDTO> updateVerificationStatus(String id, VerificationStatus status);

    Mono<Void> deleteDriverProfile(String id);
}