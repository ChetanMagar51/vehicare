package com.vehicare.modules.owner.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vehicare.common.api.ApiResponse;
import com.vehicare.modules.auth.security.UserPrincipal;
import com.vehicare.modules.owner.api.OwnerService;
import com.vehicare.modules.owner.dto.OwnerDto;
import com.vehicare.modules.owner.dto.UpdateOwnerRequest;
import com.vehicare.modules.vehicle.dto.VehicleDto;
import com.vehicare.modules.vehicle.dto.VehicleRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/owner")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<OwnerDto>> getProfile(
            Authentication authentication) {

        Long ownerId = getUserId(authentication);

        OwnerDto owner = ownerService.getProfile(ownerId);

        return ResponseEntity.ok(
                ApiResponse.<OwnerDto>builder()
                        .status("Success")
                        .message("Owner profile fetched successfully")
                        .data(owner)
                        .build()
        );
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<OwnerDto>> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateOwnerRequest request) {

        Long ownerId = getUserId(authentication);

        OwnerDto owner =
                ownerService.updateProfile(ownerId, request);

        return ResponseEntity.ok(
                ApiResponse.<OwnerDto>builder()
                        .status("Success")
                        .message("Owner profile updated successfully")
                        .data(owner)
                        .build()
        );
    }

    @PostMapping("/vehicles")
    public ResponseEntity<ApiResponse<VehicleDto>> addVehicle(
            Authentication authentication,
            @Valid @RequestBody VehicleRequest request) {

        Long ownerId = getUserId(authentication);

        VehicleDto vehicle =
                ownerService.addVehicle(ownerId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<VehicleDto>builder()
                                .status("Success")
                                .message("Vehicle added successfully")
                                .data(vehicle)
                                .build()
                );
    }

    @GetMapping("/vehicles")
    public ResponseEntity<ApiResponse<List<VehicleDto>>> getMyVehicles(
            Authentication authentication) {

        Long ownerId = getUserId(authentication);

        List<VehicleDto> vehicles =
                ownerService.getMyVehicles(ownerId);

        return ResponseEntity.ok(
                ApiResponse.<List<VehicleDto>>builder()
                        .status("Success")
                        .message("Vehicles fetched successfully")
                        .data(vehicles)
                        .build()
        );
    }

    @GetMapping("/vehicles/{vehicleId}")
    public ResponseEntity<ApiResponse<VehicleDto>> getMyVehicle(
            Authentication authentication,
            @PathVariable Long vehicleId) {

        Long ownerId = getUserId(authentication);

        VehicleDto vehicle =
                ownerService.getMyVehicle(ownerId, vehicleId);

        return ResponseEntity.ok(
                ApiResponse.<VehicleDto>builder()
                        .status("Success")
                        .message("Vehicle fetched successfully")
                        .data(vehicle)
                        .build()
        );
    }

    @PutMapping("/vehicles/{vehicleId}")
    public ResponseEntity<ApiResponse<VehicleDto>> updateMyVehicle(
            Authentication authentication,
            @PathVariable Long vehicleId,
            @Valid @RequestBody VehicleRequest request) {

        Long ownerId = getUserId(authentication);

        VehicleDto vehicle =
                ownerService.updateMyVehicle(
                        ownerId,
                        vehicleId,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.<VehicleDto>builder()
                        .status("Success")
                        .message("Vehicle updated successfully")
                        .data(vehicle)
                        .build()
        );
    }

    private Long getUserId(Authentication authentication) {

        UserPrincipal principal =
                (UserPrincipal) authentication.getPrincipal();

        return principal.getId();
    }
}