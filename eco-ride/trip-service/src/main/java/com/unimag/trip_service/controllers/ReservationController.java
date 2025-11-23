package com.unimag.trip_service.controllers;

import com.unimag.trip_service.dtos.reservation.ResponseReservationDTO;
import com.unimag.trip_service.mappers.ReservationMapper;
import com.unimag.trip_service.services.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public Flux<ResponseReservationDTO> getAll(){
        return reservationService.findAll();
    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<ResponseReservationDTO>> getById(@PathVariable String id){
        return reservationService.findById(id)
                .map(dto -> ResponseEntity.ok().body(dto));
    }
}
