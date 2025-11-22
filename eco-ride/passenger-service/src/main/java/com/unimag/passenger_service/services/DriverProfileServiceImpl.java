package com.unimag.passenger_service.services;

import com.unimag.passenger_service.dtos.driver.CreateDriverProfileDTO;
import com.unimag.passenger_service.dtos.driver.ResponseDriverProfileDTO;
import com.unimag.passenger_service.dtos.driver.UpdateDriverProfileDTO;
import com.unimag.passenger_service.entities.DriverProfile;
import com.unimag.passenger_service.entities.Passenger;
import com.unimag.passenger_service.entities.VerificationStatus;
import com.unimag.passenger_service.exceptions.conflict.DriverProfileAlreadyExistsException;
import com.unimag.passenger_service.exceptions.notfound.DriverProfileNotFoundException;
import com.unimag.passenger_service.exceptions.notfound.PassengerNotFoundException;
import com.unimag.passenger_service.mappers.DriverProfileMapper;
import com.unimag.passenger_service.repositories.DriverProfileRepository;
import com.unimag.passenger_service.repositories.PassengerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverProfileServiceImpl implements DriverProfileService {

    private final DriverProfileRepository driverProfileRepository;
    private final PassengerRepository passengerRepository;
    private final DriverProfileMapper driverProfileMapper;

    @Override
    @Transactional
    public ResponseDriverProfileDTO createDriverProfile(CreateDriverProfileDTO dto) {
        log.info("Creating driver profile for passenger ID: {}", dto.passengerId());

        // Buscar el pasajero
        Passenger passenger = passengerRepository.findById(dto.passengerId())
                .orElseThrow(() -> new PassengerNotFoundException("Passenger not found with ID: " + dto.passengerId()));

        // Validar que el pasajero no tenga ya un perfil de conductor
        if (driverProfileRepository.existsByPassenger(passenger)) {
            throw new DriverProfileAlreadyExistsException("Driver profile already exists for passenger ID: " + dto.passengerId());
        }

        // Validar que la licencia no exista
        if (driverProfileRepository.existsByLicenseNo(dto.licenseNo())) {
            throw new DriverProfileAlreadyExistsException("License number " + dto.licenseNo() + " is already registered");
        }

        DriverProfile driverProfile = driverProfileMapper.toEntity(dto);
        driverProfile.setPassenger(passenger);

        DriverProfile savedProfile = driverProfileRepository.save(driverProfile);

        log.info("Driver profile created successfully with ID: {}", savedProfile.getId());
        return driverProfileMapper.toResponseDTO(savedProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseDriverProfileDTO getDriverProfileById(String id) {
        log.info("Fetching driver profile with ID: {}", id);

        DriverProfile driverProfile = driverProfileRepository.findById(id)
                .orElseThrow(() -> new DriverProfileNotFoundException("Driver profile not found with ID: " + id));

        return driverProfileMapper.toResponseDTO(driverProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseDriverProfileDTO getDriverProfileByPassengerId(String passengerId) {
        log.info("Fetching driver profile for passenger ID: {}", passengerId);

        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new PassengerNotFoundException("Passenger not found with ID: " + passengerId));

        DriverProfile driverProfile = driverProfileRepository.findByPassenger(passenger)
                .orElseThrow(() -> new DriverProfileNotFoundException("Driver profile not found for passenger ID: " + passengerId));

        return driverProfileMapper.toResponseDTO(driverProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseDriverProfileDTO> getAllDriverProfiles() {
        log.info("Fetching all driver profiles");

        return driverProfileRepository.findAll().stream()
                .map(driverProfileMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResponseDriverProfileDTO updateDriverProfile(String id, UpdateDriverProfileDTO dto) {
        log.info("Updating driver profile with ID: {}", id);

        DriverProfile driverProfile = driverProfileRepository.findById(id)
                .orElseThrow(() -> new DriverProfileNotFoundException("Driver profile not found with ID: " + id));

        driverProfileMapper.updateEntityFromDTO(dto, driverProfile);
        DriverProfile updatedProfile = driverProfileRepository.save(driverProfile);

        log.info("Driver profile updated successfully with ID: {}", updatedProfile.getId());
        return driverProfileMapper.toResponseDTO(updatedProfile);
    }

    @Override
    @Transactional
    public ResponseDriverProfileDTO updateVerificationStatus(String id, VerificationStatus status) {
        log.info("Updating verification status for driver profile ID: {} to {}", id, status);

        DriverProfile driverProfile = driverProfileRepository.findById(id)
                .orElseThrow(() -> new DriverProfileNotFoundException("Driver profile not found with ID: " + id));

        driverProfile.setVerificationStatus(status);
        DriverProfile updatedProfile = driverProfileRepository.save(driverProfile);

        log.info("Verification status updated successfully");
        return driverProfileMapper.toResponseDTO(updatedProfile);
    }

    @Override
    @Transactional
    public void deleteDriverProfile(String id) {
        log.info("Deleting driver profile with ID: {}", id);

        if (!driverProfileRepository.existsById(id)) {
            throw new DriverProfileNotFoundException("Driver profile not found with ID: " + id);
        }

        driverProfileRepository.deleteById(id);
        log.info("Driver profile deleted successfully with ID: {}", id);
    }
}