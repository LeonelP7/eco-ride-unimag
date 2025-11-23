package com.unimag.apigateway.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/trips")
    public Mono<ResponseEntity<Map<String, Object>>> tripsFallback() {
        return Mono.fromSupplier(() -> {
            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", LocalDateTime.now());
            response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
            response.put("error", "Service Unavailable");
            response.put("message", "El servicio de viajes no está disponible temporalmente. Por favor, intenta nuevamente.");
            response.put("service", "trips-service");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        });
    }


    @GetMapping("/payments")
    public Mono<ResponseEntity<Map<String, Object>>> paymentsFallback() {
        return Mono.fromSupplier(() -> {
            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", LocalDateTime.now());
            response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
            response.put("error", "Service Unavailable");
            response.put("message", "El servicio de pagos no está disponible.");
            response.put("service", "payments-service");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        });
    }


    @GetMapping("/notifications")
    public Mono<ResponseEntity<Map<String, Object>>> notificationsFallback() {
        return Mono.fromSupplier(() -> {
            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", LocalDateTime.now());
            response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
            response.put("error", "Service Unavailable");
            response.put("message", "El servicio de notificaciones no está disponible.");
            response.put("service", "notifications-service");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        });
    }


    @GetMapping("/passengers")
    public Mono<ResponseEntity<Map<String, Object>>> usersFallback() {
        return Mono.fromSupplier(() -> {
            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", LocalDateTime.now());
            response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
            response.put("error", "Service Unavailable");
            response.put("message", "El servicio de pasajeros no está disponible temporalmente.");
            response.put("service", "users-service");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        });
    }
}
