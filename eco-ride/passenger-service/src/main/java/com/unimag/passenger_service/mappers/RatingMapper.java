package com.unimag.passenger_service.mappers;

import com.unimag.passenger_service.dtos.rating.CreateRatingDTO;
import com.unimag.passenger_service.dtos.rating.ResponseRatingDTO;
import com.unimag.passenger_service.entities.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    @Mapping(source = "fromPassengerId", target = "fromId")
    @Mapping(source = "toPassengerId", target = "toId")
    Rating toEntity(CreateRatingDTO createRatingDTO);

    @Mapping(source = "fromId", target = "fromPassengerId")
    @Mapping(source = "toId", target = "toPassengerId")
    ResponseRatingDTO toResponseDTO(Rating rating);
}