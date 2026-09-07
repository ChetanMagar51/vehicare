package com.vehicare.modules.owner.api;

import java.util.List;

import com.vehicare.modules.owner.dto.OwnerDto;
import com.vehicare.modules.owner.dto.UpdateOwnerRequest;
import com.vehicare.modules.vehicle.dto.VehicleDto;
import com.vehicare.modules.vehicle.dto.VehicleRequest;

public interface OwnerService {

    OwnerDto getProfile(Long ownerId);

    OwnerDto updateProfile(
            Long ownerId,
            UpdateOwnerRequest request);

    VehicleDto addVehicle(
            Long ownerId,
            VehicleRequest request);

    List<VehicleDto> getMyVehicles(Long ownerId);

    VehicleDto getMyVehicle(
            Long ownerId,
            Long vehicleId);

    VehicleDto updateMyVehicle(
            Long ownerId,
            Long vehicleId,
            VehicleRequest request);
}