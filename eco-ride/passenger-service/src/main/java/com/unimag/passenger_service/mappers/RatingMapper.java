package com.unimag.passenger_service.mappers;

import com.unimag.passenger_service.dtos.rating.CreateRatingDTO;
import com.unimag.passenger_service.dtos.rating.ResponseRatingDTO;
import com.unimag.passenger_service.entities.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    @Mapping(target = "fromPassenger", ignore = true)
    @Mapping(target = "toPassenger", ignore = true)
    Rating toEntity(CreateRatingDTO dto);

    @Mapping(source = "fromPassenger.name", target = "fromPassengerName")
    @Mapping(source = "toPassenger.name", target = "toPassengerName")
    ResponseRatingDTO toResponseDTO(Rating rating);
}