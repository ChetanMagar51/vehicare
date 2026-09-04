package com.vehicare.modules.vehicle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.vehicare.modules.vehicle.entity.FuelType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDto {

    private Long id;

    private Long ownerId;

    private String registrationNumber;

    private String make;

    private String model;

    private Integer manufacturingYear;

    private FuelType fuelType;

    private boolean active;
}