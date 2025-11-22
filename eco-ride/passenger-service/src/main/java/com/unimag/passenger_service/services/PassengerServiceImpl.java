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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final PassengerMapper passengerMapper;

    @Override
    @Transactional
    public ResponsePassengerDTO createPassenger(CreatePassengerDTO dto) {
        log.info("Creating passenger with email: {}", dto.email());

        // Validar que no exista por email
        if (passengerRepository.existsByEmail(dto.email())) {
            throw new PassengerAlreadyExistsException("Passenger with email " + dto.email() + " already exists");
        }

        // Validar que no exista por keycloakSub
        if (passengerRepository.existsByKeycloakSub(dto.keycloakSub())) {
            throw new PassengerAlreadyExistsException("Passenger with Keycloak sub " + dto.keycloakSub() + " already exists");
        }

        Passenger passenger = passengerMapper.toEntity(dto);
        Passenger savedPassenger = passengerRepository.save(passenger);

        log.info("Passenger created successfully with ID: {}", savedPassenger.getId());
        return passengerMapper.toResponseDTO(savedPassenger);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponsePassengerDTO getPassengerById(String id) {
        log.info("Fetching passenger with ID: {}", id);

        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new PassengerNotFoundException("Passenger not found with ID: " + id));

        return passengerMapper.toResponseDTO(passenger);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponsePassengerDTO getPassengerByKeycloakSub(String keycloakSub) {
        log.info("Fetching passenger with Keycloak sub: {}", keycloakSub);

        Passenger passenger = passengerRepository.findByKeycloakSub(keycloakSub)
                .orElseThrow(() -> new PassengerNotFoundException("Passenger not found with Keycloak sub: " + keycloakSub));

        return passengerMapper.toResponseDTO(passenger);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponsePassengerDTO getPassengerByEmail(String email) {
        log.info("Fetching passenger with email: {}", email);

        Passenger passenger = passengerRepository.findByEmail(email)
                .orElseThrow(() -> new PassengerNotFoundException("Passenger not found with email: " + email));

        return passengerMapper.toResponseDTO(passenger);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponsePassengerDTO> getAllPassengers() {
        log.info("Fetching all passengers");

        return passengerRepository.findAll().stream()
                .map(passengerMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResponsePassengerDTO updatePassenger(String id, UpdatePassengerDTO dto) {
        log.info("Updating passenger with ID: {}", id);

        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new PassengerNotFoundException("Passenger not found with ID: " + id));

        // Validar email único si se está cambiando
        if (dto.email() != null && !dto.email().equals(passenger.getEmail())) {
            if (passengerRepository.existsByEmail(dto.email())) {
                throw new PassengerAlreadyExistsException("Email " + dto.email() + " is already in use");
            }
        }

        passengerMapper.updateEntityFromDTO(dto, passenger);
        Passenger updatedPassenger = passengerRepository.save(passenger);

        log.info("Passenger updated successfully with ID: {}", updatedPassenger.getId());
        return passengerMapper.toResponseDTO(updatedPassenger);
    }

    @Override
    @Transactional
    public void deletePassenger(String id) {
        log.info("Deleting passenger with ID: {}", id);

        if (!passengerRepository.existsById(id)) {
            throw new PassengerNotFoundException("Passenger not found with ID: " + id);
        }

        passengerRepository.deleteById(id);
        log.info("Passenger deleted successfully with ID: {}", id);
    }
}