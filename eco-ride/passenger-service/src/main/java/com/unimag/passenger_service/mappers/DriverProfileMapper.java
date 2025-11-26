package com.unimag.passenger_service.mappers;

import com.unimag.passenger_service.dtos.driver.CreateDriverProfileDTO;
import com.unimag.passenger_service.dtos.driver.ResponseDriverProfileDTO;
import com.unimag.passenger_service.dtos.driver.UpdateDriverProfileDTO;
import com.unimag.passenger_service.entities.DriverProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface DriverProfileMapper {

    @Mapping(target = "passenger", ignore = true)
    DriverProfile toEntity(CreateDriverProfileDTO dto);

    @Mapping(source = "passenger.id", target = "passengerId")
    @Mapping(source = "passenger.name", target = "passengerName")
    ResponseDriverProfileDTO toResponseDTO(DriverProfile driverProfile);

    void updateEntityFromDTO(UpdateDriverProfileDTO dto, @MappingTarget DriverProfile driverProfile);
}