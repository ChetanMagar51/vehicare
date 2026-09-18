package com.vehicare.modules.vehicle.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vehicare.modules.user.dto.UserDto;
import com.vehicare.modules.user.entity.Role;
import com.vehicare.modules.user.service.UserService;
import com.vehicare.modules.vehicle.api.VehicleService;
import com.vehicare.modules.vehicle.dto.VehicleDto;
import com.vehicare.modules.vehicle.dto.VehicleRequest;
import com.vehicare.modules.vehicle.entity.Vehicle;
import com.vehicare.modules.vehicle.exception.InvalidVehicleOwnerException;
import com.vehicare.modules.vehicle.exception.VehicleNotFoundException;
import com.vehicare.modules.vehicle.exception.VehicleRegistrationAlreadyExistsException;
import com.vehicare.modules.vehicle.repository.VehicleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserService userService;

    @Override
    public VehicleDto createVehicle(VehicleRequest request) {

        // 1. Validate owner
        validateOwner(request.getOwnerId());

        // 2. Check duplicate registration number
        if (vehicleRepository.existsByRegistrationNumber(
                request.getRegistrationNumber())) {

        	throw new VehicleRegistrationAlreadyExistsException(
        	        "Vehicle already exists with registration number: "
        	                + request.getRegistrationNumber());

        }

        // 3. Create vehicle
        Vehicle vehicle = Vehicle.builder()
                .ownerId(request.getOwnerId())
                .registrationNumber(request.getRegistrationNumber())
                .make(request.getMake())
                .model(request.getModel())
                .manufacturingYear(request.getManufacturingYear())
                .fuelType(request.getFuelType())
                .active(true)
                .build();

        // 4. Save
        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        return mapToDto(savedVehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleDto getVehicleById(Long id) {

        Vehicle vehicle = findVehicleById(id);

        return mapToDto(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleDto getVehicleByRegistrationNumber(
            String registrationNumber) {

        Vehicle vehicle = vehicleRepository
                .findByRegistrationNumber(registrationNumber)
                .orElseThrow(() -> new VehicleNotFoundException(
                        "Vehicle not found with registration number: "
                                + registrationNumber));

        return mapToDto(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleDto> getAllVehicles() {

        return vehicleRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleDto> getVehiclesByOwner(Long ownerId) {

        // Validate that owner exists and has OWNER role
        validateOwner(ownerId);

        return vehicleRepository.findAllByOwnerId(ownerId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public VehicleDto updateVehicle(
            Long id,
            VehicleRequest request) {

        Vehicle vehicle = findVehicleById(id);

        // Validate new owner
        validateOwner(request.getOwnerId());

        // Check registration number only if it is being changed
        if (!vehicle.getRegistrationNumber()
                .equals(request.getRegistrationNumber())) {

            if (vehicleRepository.existsByRegistrationNumber(
                    request.getRegistrationNumber())) {

            	throw new VehicleRegistrationAlreadyExistsException(
            	        "Vehicle already exists with registration number: "
            	                + request.getRegistrationNumber());
            }
        }

        vehicle.setOwnerId(request.getOwnerId());
        vehicle.setRegistrationNumber(request.getRegistrationNumber());
        vehicle.setMake(request.getMake());
        vehicle.setModel(request.getModel());
        vehicle.setManufacturingYear(request.getManufacturingYear());
        vehicle.setFuelType(request.getFuelType());

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);

        return mapToDto(updatedVehicle);
    }

    @Override
    public void activateVehicle(Long id) {

        Vehicle vehicle = findVehicleById(id);

        vehicle.setActive(true);

        vehicleRepository.save(vehicle);
    }

    @Override
    public void deactivateVehicle(Long id) {

        Vehicle vehicle = findVehicleById(id);

        vehicle.setActive(false);

        vehicleRepository.save(vehicle);
    }

    /**
     * Validates that the given user exists and is an OWNER.
     */
    private void validateOwner(Long ownerId) {

        UserDto owner = userService.getUserById(ownerId);

        if (owner.getRole() != Role.Owner) {
        	throw new InvalidVehicleOwnerException(
        	        "User with id " + ownerId + " is not an OWNER");
        }
    }

    /**
     * Finds vehicle or throws exception.
     */
    private Vehicle findVehicleById(Long id) {

        return vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException(
                        "Vehicle not found with id: " + id));
    }

    /**
     * Converts Entity to DTO.
     */
    private VehicleDto mapToDto(Vehicle vehicle) {

        return VehicleDto.builder()
                .id(vehicle.getId())
                .ownerId(vehicle.getOwnerId())
                .registrationNumber(vehicle.getRegistrationNumber())
                .make(vehicle.getMake())
                .model(vehicle.getModel())
                .manufacturingYear(vehicle.getManufacturingYear())
                .fuelType(vehicle.getFuelType())
                .active(vehicle.isActive())
                .build();
    }
}