package com.unimag.passenger_service.mappers;

import com.unimag.passenger_service.dtos.driver.CreateDriverProfileDTO;
import com.unimag.passenger_service.dtos.driver.ResponseDriverProfileDTO;
import com.unimag.passenger_service.dtos.driver.UpdateDriverProfileDTO;
import com.unimag.passenger_service.entities.DriverProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DriverProfileMapper {

    // ✅ CreateDTO → Entity
    DriverProfile toEntity(CreateDriverProfileDTO createDriverProfileDTO);

    // ✅ Entity → ResponseDTO
    ResponseDriverProfileDTO toResponseDTO(DriverProfile driverProfile);

    // ✅ UpdateDTO → Entity (para actualizar)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passengerId", ignore = true)
    void updateEntityFromDTO(UpdateDriverProfileDTO updateDriverProfileDTO, @MappingTarget DriverProfile driverProfile);
}