package com.unimag.passenger_service.controllers;

import com.unimag.passenger_service.dtos.driver.CreateDriverProfileDTO;
import com.unimag.passenger_service.dtos.driver.ResponseDriverProfileDTO;
import com.unimag.passenger_service.dtos.driver.UpdateDriverProfileDTO;
import com.unimag.passenger_service.enums.VerificationStatus;
import com.unimag.passenger_service.services.DriverProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/driver-profiles")
public class DriverProfileController {

    private final DriverProfileService driverProfileService;

    @PostMapping
    public Mono<ResponseEntity<ResponseDriverProfileDTO>> createDriverProfile(
            @Valid @RequestBody CreateDriverProfileDTO dto) {
        return driverProfileService.createDriverProfile(dto)
                .map(profile -> ResponseEntity.status(HttpStatus.CREATED).body(profile));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ResponseDriverProfileDTO>> getDriverProfileById(@PathVariable String id) {
        return driverProfileService.getDriverProfileById(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/passenger/{passengerId}")
    public Mono<ResponseEntity<ResponseDriverProfileDTO>> getDriverProfileByPassengerId(
            @PathVariable String passengerId) {
        return driverProfileService.getDriverProfileByPassengerId(passengerId)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<ResponseDriverProfileDTO> getAllDriverProfiles() {
        return driverProfileService.getAllDriverProfiles();
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ResponseDriverProfileDTO>> updateDriverProfile(
            @PathVariable String id,
            @Valid @RequestBody UpdateDriverProfileDTO dto) {
        return driverProfileService.updateDriverProfile(id, dto)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{id}/verification-status")
    public Mono<ResponseEntity<ResponseDriverProfileDTO>> updateVerificationStatus(
            @PathVariable String id,
            @RequestParam VerificationStatus status) {
        return driverProfileService.updateVerificationStatus(id, status)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteDriverProfile(@PathVariable String id) {
        return driverProfileService.deleteDriverProfile(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
}