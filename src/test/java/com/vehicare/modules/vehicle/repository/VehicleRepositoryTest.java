package com.vehicare.modules.vehicle.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.vehicare.modules.vehicle.entity.FuelType;
import com.vehicare.modules.vehicle.entity.Vehicle;

@DataJpaTest
class VehicleRepositoryTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    private Vehicle vehicle1;
    private Vehicle vehicle2;

    @BeforeEach
    void setUp() {

        vehicle1 = Vehicle.builder()
                .ownerId(1L)
                .registrationNumber("MH12AB1234")
                .make("Toyota")
                .model("Fortuner")
                .manufacturingYear(2022)
                .fuelType(FuelType.PETROL)
                .active(true)
                .build();

        vehicle2 = Vehicle.builder()
                .ownerId(1L)
                .registrationNumber("MH12CD5678")
                .make("Honda")
                .model("City")
                .manufacturingYear(2023)
                .fuelType(FuelType.PETROL)
                .active(true)
                .build();

        vehicleRepository.save(vehicle1);
        vehicleRepository.save(vehicle2);
    }


    // =========================================================
    // existsByRegistrationNumber()
    // =========================================================

    @Test
    void existsByRegistrationNumber_ShouldReturnTrue_WhenRegistrationNumberExists() {

        boolean result =
                vehicleRepository.existsByRegistrationNumber("MH12AB1234");

        assertTrue(result);
    }

    @Test
    void existsByRegistrationNumber_ShouldReturnFalse_WhenRegistrationNumberDoesNotExist() {

        boolean result =
                vehicleRepository.existsByRegistrationNumber("MH99XX9999");

        assertFalse(result);
    }


    // =========================================================
    // findByRegistrationNumber()
    // =========================================================

    @Test
    void findByRegistrationNumber_ShouldReturnVehicle_WhenRegistrationNumberExists() {

        Optional<Vehicle> result =
                vehicleRepository.findByRegistrationNumber("MH12AB1234");

        assertTrue(result.isPresent());

        assertEquals(
                "Toyota",
                result.get().getMake()
        );

        assertEquals(
                "Fortuner",
                result.get().getModel()
        );
    }

    @Test
    void findByRegistrationNumber_ShouldReturnEmpty_WhenRegistrationNumberDoesNotExist() {

        Optional<Vehicle> result =
                vehicleRepository.findByRegistrationNumber("MH99XX9999");

        assertTrue(result.isEmpty());
    }


    // =========================================================
    // findAllByOwnerId()
    // =========================================================

    @Test
    void findAllByOwnerId_ShouldReturnVehicles_WhenOwnerHasVehicles() {

        List<Vehicle> result =
                vehicleRepository.findAllByOwnerId(1L);

        assertEquals(2, result.size());

        assertEquals(
                "MH12AB1234",
                result.get(0).getRegistrationNumber()
        );

        assertEquals(
                "MH12CD5678",
                result.get(1).getRegistrationNumber()
        );
    }

    @Test
    void findAllByOwnerId_ShouldReturnEmpty_WhenOwnerHasNoVehicles() {

        List<Vehicle> result =
                vehicleRepository.findAllByOwnerId(999L);

        assertTrue(result.isEmpty());
    }
}