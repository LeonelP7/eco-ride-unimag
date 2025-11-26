package com.unimag.passenger_service.mappers;

import com.unimag.passenger_service.dtos.passenger.CreatePassengerDTO;
import com.unimag.passenger_service.dtos.passenger.ResponsePassengerDTO;
import com.unimag.passenger_service.dtos.passenger.UpdatePassengerDTO;
import com.unimag.passenger_service.entities.Passenger;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PassengerMapper {

    // ✅ CreateDTO → Entity
    Passenger toEntity(CreatePassengerDTO createPassengerDTO);

    // ✅ Entity → ResponseDTO
    ResponsePassengerDTO toResponseDTO(Passenger passenger);

    // ✅ UpdateDTO → Entity (para actualizar)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "keycloakSub", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDTO(UpdatePassengerDTO updatePassengerDTO, @MappingTarget Passenger passenger);
}