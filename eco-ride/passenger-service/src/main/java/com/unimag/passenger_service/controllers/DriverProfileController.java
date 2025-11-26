package com.unimag.passenger_service.controllers;

import com.unimag.passenger_service.dtos.driver.CreateDriverProfileDTO;
import com.unimag.passenger_service.dtos.driver.ResponseDriverProfileDTO;
import com.unimag.passenger_service.dtos.driver.UpdateDriverProfileDTO;
import com.unimag.passenger_service.enums.VerificationStatus;
import com.unimag.passenger_service.services.DriverProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/driver-profiles")
@Tag(name = "Driver Profile Management", description = "API for managing driver profiles and verification")
public class DriverProfileController {

    private final DriverProfileService driverProfileService;

    @PostMapping
    @Operation(
            summary = "Create driver profile",
            description = "Creates a new driver profile for a passenger who wants to become a driver"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Driver profile created successfully",
                    content = @Content(schema = @Schema(implementation = ResponseDriverProfileDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Driver profile already exists for this passenger"
            )
    })
    public Mono<ResponseEntity<ResponseDriverProfileDTO>> createDriverProfile(
            @Valid @RequestBody CreateDriverProfileDTO dto) {
        return driverProfileService.createDriverProfile(dto)
                .map(profile -> ResponseEntity.status(HttpStatus.CREATED).body(profile));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get driver profile by ID",
            description = "Retrieves a specific driver profile using its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Driver profile found",
                    content = @Content(schema = @Schema(implementation = ResponseDriverProfileDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Driver profile not found"
            )
    })
    public Mono<ResponseEntity<ResponseDriverProfileDTO>> getDriverProfileById(
            @Parameter(description = "Driver profile ID", required = true)
            @PathVariable String id) {
        return driverProfileService.getDriverProfileById(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/passenger/{passengerId}")
    @Operation(
            summary = "Get driver profile by passenger ID",
            description = "Retrieves the driver profile associated with a specific passenger"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Driver profile found",
                    content = @Content(schema = @Schema(implementation = ResponseDriverProfileDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Driver profile not found for this passenger"
            )
    })
    public Mono<ResponseEntity<ResponseDriverProfileDTO>> getDriverProfileByPassengerId(
            @Parameter(description = "Passenger ID", required = true)
            @PathVariable String passengerId) {
        return driverProfileService.getDriverProfileByPassengerId(passengerId)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    @Operation(
            summary = "Get all driver profiles",
            description = "Retrieves a list of all driver profiles in the system"
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of driver profiles retrieved successfully"
    )
    public Flux<ResponseDriverProfileDTO> getAllDriverProfiles() {
        return driverProfileService.getAllDriverProfiles();
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update driver profile",
            description = "Updates an existing driver profile with new information"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Driver profile updated successfully",
                    content = @Content(schema = @Schema(implementation = ResponseDriverProfileDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Driver profile not found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            )
    })
    public Mono<ResponseEntity<ResponseDriverProfileDTO>> updateDriverProfile(
            @Parameter(description = "Driver profile ID", required = true)
            @PathVariable String id,
            @Valid @RequestBody UpdateDriverProfileDTO dto) {
        return driverProfileService.updateDriverProfile(id, dto)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{id}/verification-status")
    @Operation(
            summary = "Update driver verification status",
            description = "Updates the verification status of a driver profile (PENDING, VERIFIED, REJECTED)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Verification status updated successfully",
                    content = @Content(schema = @Schema(implementation = ResponseDriverProfileDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Driver profile not found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid verification status"
            )
    })
    public Mono<ResponseEntity<ResponseDriverProfileDTO>> updateVerificationStatus(
            @Parameter(description = "Driver profile ID", required = true)
            @PathVariable String id,
            @Parameter(description = "New verification status", required = true)
            @RequestParam VerificationStatus status) {
        return driverProfileService.updateVerificationStatus(id, status)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete driver profile",
            description = "Permanently deletes a driver profile from the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Driver profile deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Driver profile not found"
            )
    })
    public Mono<ResponseEntity<Void>> deleteDriverProfile(
            @Parameter(description = "Driver profile ID", required = true)
            @PathVariable String id) {
        return driverProfileService.deleteDriverProfile(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
}