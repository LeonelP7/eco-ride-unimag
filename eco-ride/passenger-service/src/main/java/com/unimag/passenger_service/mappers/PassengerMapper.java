package com.unimag.passenger_service.mappers;

import com.unimag.passenger_service.dtos.passenger.CreatePassengerDTO;
import com.unimag.passenger_service.dtos.passenger.ResponsePassengerDTO;
import com.unimag.passenger_service.dtos.passenger.UpdatePassengerDTO;
import com.unimag.passenger_service.entities.Passenger;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PassengerMapper {

    Passenger toEntity(CreatePassengerDTO dto);

    ResponsePassengerDTO toResponseDTO(Passenger passenger);

    void updateEntityFromDTO(UpdatePassengerDTO dto, @MappingTarget Passenger passenger);
}