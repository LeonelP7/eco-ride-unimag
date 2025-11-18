package com.unimag.trip_service.mappers;

import com.unimag.trip_service.dtos.reservation.CreateReservationDTO;
import com.unimag.trip_service.dtos.reservation.ResponseReservationDTO;
import com.unimag.trip_service.entities.Reservation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    Reservation createReservationDTOToReservation(CreateReservationDTO createReservationDTO);
    ResponseReservationDTO reservationToResponseDTO(Reservation reservation);
}
