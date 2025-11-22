package com.unimag.passenger_service.dtos.driver;

import com.unimag.passenger_service.entities.VerificationStatus;

import java.time.LocalDateTime;

public record ResponseDriverProfileDTO(
        String id,
        String passengerId,
        String passengerName,
        String licenseNo,
        String carPlate,
        String carModel,
        String carColor,
        Integer seatsOffered,
        VerificationStatus verificationStatus,
        LocalDateTime createdAt
) {
}