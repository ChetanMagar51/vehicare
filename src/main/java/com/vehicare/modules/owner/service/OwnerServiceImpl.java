package com.vehicare.modules.owner.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vehicare.modules.owner.api.OwnerService;
import com.vehicare.modules.owner.dto.OwnerDto;
import com.vehicare.modules.owner.dto.UpdateOwnerRequest;
import com.vehicare.modules.owner.exception.InvalidOwnerException;
import com.vehicare.modules.owner.exception.VehicleOwnershipException;
import com.vehicare.modules.user.dto.UserDto;
import com.vehicare.modules.user.entity.Role;
import com.vehicare.modules.user.service.UserService;
import com.vehicare.modules.vehicle.api.VehicleService;
import com.vehicare.modules.vehicle.dto.VehicleDto;
import com.vehicare.modules.vehicle.dto.VehicleRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OwnerServiceImpl implements OwnerService {

	private final UserService userService;
	private final VehicleService vehicleService;

	@Override
	@Transactional(readOnly = true)
	public OwnerDto getProfile(Long ownerId) {

		UserDto user = getOwner(ownerId);

		return mapToOwnerDto(user);
	}

	@Override
	public OwnerDto updateProfile(Long ownerId, UpdateOwnerRequest request) {

		getOwner(ownerId);

		UpdateOwnerRequest userRequest = UpdateOwnerRequest.builder().firstName(request.getFirstName())
				.lastName(request.getLastName()).phone(request.getPhone()).email(request.getEmail())
				.address(request.getAddress()).build();

		// code need user service method
		return null;
	}

	@Override
	public VehicleDto addVehicle(Long ownerId, VehicleRequest request) {

		getOwner(ownerId);

		VehicleRequest vehicleRequest = VehicleRequest.builder().ownerId(ownerId)
				.registrationNumber(request.getRegistrationNumber()).make(request.getMake()).model(request.getModel())
				.manufacturingYear(request.getManufacturingYear()).fuelType(request.getFuelType()).build();

		return vehicleService.createVehicle(vehicleRequest);
	}

	@Override
	@Transactional(readOnly = true)
	public List<VehicleDto> getMyVehicles(Long ownerId) {

		getOwner(ownerId);

		return vehicleService.getVehiclesByOwner(ownerId);
	}

	@Override
	@Transactional(readOnly = true)
	public VehicleDto getMyVehicle(Long ownerId, Long vehicleId) {

		getOwner(ownerId);

		VehicleDto vehicle = vehicleService.getVehicleById(vehicleId);

		validateVehicleOwnership(vehicle, ownerId);

		return vehicle;
	}

	@Override
	public VehicleDto updateMyVehicle(Long ownerId, Long vehicleId, VehicleRequest request) {

		getOwner(ownerId);

		VehicleDto existingVehicle = vehicleService.getVehicleById(vehicleId);

		validateVehicleOwnership(existingVehicle, ownerId);

		VehicleRequest vehicleRequest = VehicleRequest.builder().ownerId(ownerId)
				.registrationNumber(request.getRegistrationNumber()).make(request.getMake()).model(request.getModel())
				.manufacturingYear(request.getManufacturingYear()).fuelType(request.getFuelType()).build();

		return vehicleService.updateVehicle(vehicleId, vehicleRequest);
	}

	private UserDto getOwner(Long ownerId) {

		UserDto user = userService.getUserById(ownerId);

		if (user.getRole() != Role.Owner) {
			throw new InvalidOwnerException("User with id " + ownerId + " is not an OWNER");
		}

		return user;
	}

	private void validateVehicleOwnership(VehicleDto vehicle, Long ownerId) {

		if (!vehicle.getOwnerId().equals(ownerId)) {
			throw new VehicleOwnershipException("Vehicle does not belong to this owner");
		}
	}

	private OwnerDto mapToOwnerDto(UserDto user) {

		return OwnerDto.builder().id(user.getId()).firstName(user.getFirstName()).lastName(user.getLastName())
				.phone(user.getPhone()).email(user.getEmail()).address(user.getAddress())

				.build();
	}
}