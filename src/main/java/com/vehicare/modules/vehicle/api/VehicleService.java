package com.vehicare.modules.vehicle.api;

import java.util.List;

import com.vehicare.modules.vehicle.dto.VehicleDto;
import com.vehicare.modules.vehicle.dto.VehicleRequest;

public interface VehicleService {

    VehicleDto createVehicle(VehicleRequest request);

    VehicleDto getVehicleById(Long id);

    VehicleDto getVehicleByRegistrationNumber(String registrationNumber);

    List<VehicleDto> getAllVehicles();

    List<VehicleDto> getVehiclesByOwner(Long ownerId);

    VehicleDto updateVehicle(Long id, VehicleRequest request);

    void activateVehicle(Long id);

    void deactivateVehicle(Long id);
}