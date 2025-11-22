package com.unimag.passenger_service.services;

import com.unimag.passenger_service.dtos.driver.CreateDriverProfileDTO;
import com.unimag.passenger_service.dtos.driver.ResponseDriverProfileDTO;
import com.unimag.passenger_service.dtos.driver.UpdateDriverProfileDTO;
import com.unimag.passenger_service.entities.VerificationStatus;

import java.util.List;

public interface DriverProfileService {

    ResponseDriverProfileDTO createDriverProfile(CreateDriverProfileDTO dto);

    ResponseDriverProfileDTO getDriverProfileById(String id);

    ResponseDriverProfileDTO getDriverProfileByPassengerId(String passengerId);

    List<ResponseDriverProfileDTO> getAllDriverProfiles();

    ResponseDriverProfileDTO updateDriverProfile(String id, UpdateDriverProfileDTO dto);

    ResponseDriverProfileDTO updateVerificationStatus(String id, VerificationStatus status);

    void deleteDriverProfile(String id);
}