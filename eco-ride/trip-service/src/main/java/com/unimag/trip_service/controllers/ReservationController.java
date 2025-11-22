package com.unimag.trip_service.controllers;

import com.unimag.trip_service.dtos.reservation.ResponseReservationDTO;
import com.unimag.trip_service.services.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<ResponseReservationDTO>> getAll(){
        return ResponseEntity.ok(reservationService.findAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<ResponseReservationDTO> getById(@PathVariable String id){
        return ResponseEntity.ok(reservationService.findById(id));
    }
}
