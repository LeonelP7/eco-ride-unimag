package com.unimag.passenger_service.services;

import com.unimag.passenger_service.dtos.passenger.CreatePassengerDTO;
import com.unimag.passenger_service.dtos.passenger.ResponsePassengerDTO;
import com.unimag.passenger_service.dtos.passenger.UpdatePassengerDTO;

import java.util.List;

public interface PassengerService {

    ResponsePassengerDTO createPassenger(CreatePassengerDTO dto);

    ResponsePassengerDTO getPassengerById(String id);

    ResponsePassengerDTO getPassengerByKeycloakSub(String keycloakSub);

    ResponsePassengerDTO getPassengerByEmail(String email);

    List<ResponsePassengerDTO> getAllPassengers();

    ResponsePassengerDTO updatePassenger(String id, UpdatePassengerDTO dto);

    void deletePassenger(String id);
}