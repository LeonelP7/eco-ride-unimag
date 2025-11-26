package com.unimag.passenger_service.services;

import com.unimag.passenger_service.dtos.passenger.CreatePassengerDTO;
import com.unimag.passenger_service.dtos.passenger.ResponsePassengerDTO;
import com.unimag.passenger_service.dtos.passenger.UpdatePassengerDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PassengerService {

    // ✅ Crear pasajero (retorna Mono)
    Mono<ResponsePassengerDTO> createPassenger(CreatePassengerDTO dto);

    // ✅ Obtener por ID
    Mono<ResponsePassengerDTO> getPassengerById(String id);

    // ✅ Obtener por email
    Mono<ResponsePassengerDTO> getPassengerByEmail(String email);

    // ✅ Obtener por keycloakSub
    Mono<ResponsePassengerDTO> getPassengerByKeycloakSub(String keycloakSub);

    // ✅ Obtener todos (retorna Flux)
    Flux<ResponsePassengerDTO> getAllPassengers();

    // ✅ Actualizar
    Mono<ResponsePassengerDTO> updatePassenger(String id, UpdatePassengerDTO dto);

    // ✅ Eliminar (retorna Mono<Void>)
    Mono<Void> deletePassenger(String id);
}