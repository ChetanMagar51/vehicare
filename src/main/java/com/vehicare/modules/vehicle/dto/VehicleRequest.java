package com.vehicare.modules.vehicle.dto;

import com.vehicare.modules.vehicle.entity.FuelType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequest {

    @NotNull
    @Positive
    private Long ownerId;

    @NotBlank
    private String registrationNumber;

    @NotBlank
    private String make;

    @NotBlank
    private String model;

    private Integer manufacturingYear;

    @NotNull
    private FuelType fuelType;
}