package com.unimag.passenger_service.controllers;

import com.unimag.passenger_service.dtos.passenger.CreatePassengerDTO;
import com.unimag.passenger_service.dtos.passenger.ResponsePassengerDTO;
import com.unimag.passenger_service.dtos.passenger.UpdatePassengerDTO;
import com.unimag.passenger_service.services.PassengerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/passengers")
public class PassengerController {

    private final PassengerService passengerService;

    @PostMapping
    public Mono<ResponseEntity<ResponsePassengerDTO>> createPassenger(@Valid @RequestBody CreatePassengerDTO dto) {
        return passengerService.createPassenger(dto)
                .map(passenger -> ResponseEntity.status(HttpStatus.CREATED).body(passenger));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ResponsePassengerDTO>> getPassengerById(@PathVariable String id) {
        return passengerService.getPassengerById(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/email/{email}")
    public Mono<ResponseEntity<ResponsePassengerDTO>> getPassengerByEmail(@PathVariable String email) {
        return passengerService.getPassengerByEmail(email)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/keycloak/{keycloakSub}")
    public Mono<ResponseEntity<ResponsePassengerDTO>> getPassengerByKeycloakSub(@PathVariable String keycloakSub) {
        return passengerService.getPassengerByKeycloakSub(keycloakSub)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<ResponsePassengerDTO> getAllPassengers() {
        return passengerService.getAllPassengers();
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ResponsePassengerDTO>> updatePassenger(
            @PathVariable String id,
            @Valid @RequestBody UpdatePassengerDTO dto) {
        return passengerService.updatePassenger(id, dto)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deletePassenger(@PathVariable String id) {
        return passengerService.deletePassenger(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
}