
package com.vehicare.modules.vehicle.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vehicare.modules.user.dto.UserDto;
import com.vehicare.modules.user.entity.Role;
import com.vehicare.modules.user.service.UserService;
import com.vehicare.modules.vehicle.dto.VehicleDto;
import com.vehicare.modules.vehicle.dto.VehicleRequest;
import com.vehicare.modules.vehicle.entity.FuelType;
import com.vehicare.modules.vehicle.entity.Vehicle;
import com.vehicare.modules.vehicle.exception.InvalidVehicleOwnerException;
import com.vehicare.modules.vehicle.exception.VehicleNotFoundException;
import com.vehicare.modules.vehicle.exception.VehicleRegistrationAlreadyExistsException;
import com.vehicare.modules.vehicle.repository.VehicleRepository;

@ExtendWith(MockitoExtension.class)
class VehicleServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    private VehicleRequest request;
    private Vehicle vehicle;
    private UserDto owner;

    @BeforeEach
    void setUp() {

        request = VehicleRequest.builder()
                .ownerId(1L)
                .registrationNumber("MH12AB1234")
                .make("Toyota")
                .model("Fortuner")
                .manufacturingYear(2024)
                .fuelType(FuelType.DIESEL)
                .build();

        vehicle = Vehicle.builder()
                .id(10L)
                .ownerId(1L)
                .registrationNumber("MH12AB1234")
                .make("Toyota")
                .model("Fortuner")
                .manufacturingYear(2024)
                .fuelType(FuelType.DIESEL)
                .active(true)
                .build();

        owner = UserDto.builder()
                .id(1L)
                .role(Role.Owner)
                .build();
    }


    // ============================================================
    // CREATE VEHICLE
    // ============================================================

    @Test
    void createVehicle_shouldCreateVehicleSuccessfully() {

        // Arrange
        when(userService.getUserById(1L))
                .thenReturn(owner);

        when(vehicleRepository.existsByRegistrationNumber("MH12AB1234"))
                .thenReturn(false);

        when(vehicleRepository.save(any(Vehicle.class)))
                .thenReturn(vehicle);

        // Act
        VehicleDto result = vehicleService.createVehicle(request);

        // Assert
        assertNotNull(result);

        assertEquals(10L, result.getId());
        assertEquals(1L, result.getOwnerId());
        assertEquals("MH12AB1234", result.getRegistrationNumber());
        assertEquals("Toyota", result.getMake());
        assertEquals("Fortuner", result.getModel());
        assertEquals(2024, result.getManufacturingYear());
        assertEquals(FuelType.DIESEL, result.getFuelType());
        assertTrue(result.isActive());

        verify(userService).getUserById(1L);
        verify(vehicleRepository)
                .existsByRegistrationNumber("MH12AB1234");
        verify(vehicleRepository).save(any(Vehicle.class));
    }


    @Test
    void createVehicle_shouldThrowException_whenOwnerIsNotFound() {

        // Arrange
        when(userService.getUserById(1L))
                .thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        assertThrows(
                RuntimeException.class,
                () -> vehicleService.createVehicle(request)
        );

        verify(userService).getUserById(1L);

        verify(vehicleRepository, never())
                .existsByRegistrationNumber(anyString());

        verify(vehicleRepository, never())
                .save(any(Vehicle.class));
    }


    @Test
    void createVehicle_shouldThrowException_whenUserIsNotOwner() {

        // Arrange
        UserDto serviceAdvisor = UserDto.builder()
                .id(1L)
                .role(Role.Service_Adviser)
                .build();

        when(userService.getUserById(1L))
                .thenReturn(serviceAdvisor);

        // Act & Assert
        assertThrows(
                InvalidVehicleOwnerException.class,
                () -> vehicleService.createVehicle(request)
        );

        verify(userService).getUserById(1L);

        verify(vehicleRepository, never())
                .existsByRegistrationNumber(anyString());

        verify(vehicleRepository, never())
                .save(any(Vehicle.class));
    }


    @Test
    void createVehicle_shouldThrowException_whenRegistrationNumberAlreadyExists() {

        // Arrange
        when(userService.getUserById(1L))
                .thenReturn(owner);

        when(vehicleRepository.existsByRegistrationNumber("MH12AB1234"))
                .thenReturn(true);

        // Act & Assert
        VehicleRegistrationAlreadyExistsException exception =
                assertThrows(
                        VehicleRegistrationAlreadyExistsException.class,
                        () -> vehicleService.createVehicle(request)
                );

        assertTrue(exception.getMessage()
                .contains("MH12AB1234"));

        verify(userService).getUserById(1L);

        verify(vehicleRepository)
                .existsByRegistrationNumber("MH12AB1234");

        verify(vehicleRepository, never())
                .save(any(Vehicle.class));
    }


    // ============================================================
    // GET VEHICLE BY ID
    // ============================================================

    @Test
    void getVehicleById_shouldReturnVehicleSuccessfully() {

        // Arrange
        when(vehicleRepository.findById(10L))
                .thenReturn(Optional.of(vehicle));

        // Act
        VehicleDto result = vehicleService.getVehicleById(10L);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("MH12AB1234", result.getRegistrationNumber());

        verify(vehicleRepository).findById(10L);
    }


    @Test
    void getVehicleById_shouldThrowException_whenVehicleNotFound() {

        // Arrange
        when(vehicleRepository.findById(10L))
                .thenReturn(Optional.empty());

        // Act & Assert
        VehicleNotFoundException exception =
                assertThrows(
                        VehicleNotFoundException.class,
                        () -> vehicleService.getVehicleById(10L)
                );

        assertTrue(exception.getMessage()
                .contains("10"));

        verify(vehicleRepository).findById(10L);
    }


    // ============================================================
    // GET VEHICLE BY REGISTRATION NUMBER
    // ============================================================

    @Test
    void getVehicleByRegistrationNumber_shouldReturnVehicleSuccessfully() {

        // Arrange
        when(vehicleRepository
                .findByRegistrationNumber("MH12AB1234"))
                .thenReturn(Optional.of(vehicle));

        // Act
        VehicleDto result =
                vehicleService.getVehicleByRegistrationNumber(
                        "MH12AB1234"
                );

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("MH12AB1234",
                result.getRegistrationNumber());

        verify(vehicleRepository)
                .findByRegistrationNumber("MH12AB1234");
    }


    @Test
    void getVehicleByRegistrationNumber_shouldThrowException_whenVehicleNotFound() {

        // Arrange
        when(vehicleRepository
                .findByRegistrationNumber("MH12AB1234"))
                .thenReturn(Optional.empty());

        // Act & Assert
        VehicleNotFoundException exception =
                assertThrows(
                        VehicleNotFoundException.class,
                        () -> vehicleService
                                .getVehicleByRegistrationNumber(
                                        "MH12AB1234"
                                )
                );

        assertTrue(exception.getMessage()
                .contains("MH12AB1234"));

        verify(vehicleRepository)
                .findByRegistrationNumber("MH12AB1234");
    }


    // ============================================================
    // GET ALL VEHICLES
    // ============================================================

    @Test
    void getAllVehicles_shouldReturnAllVehicles() {

        // Arrange
        Vehicle vehicle2 = Vehicle.builder()
                .id(11L)
                .ownerId(2L)
                .registrationNumber("MH14XY5678")
                .make("Honda")
                .model("City")
                .manufacturingYear(2023)
                .fuelType(FuelType.PETROL)
                .active(true)
                .build();

        when(vehicleRepository.findAll())
                .thenReturn(List.of(vehicle, vehicle2));

        // Act
        List<VehicleDto> result =
                vehicleService.getAllVehicles();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("MH12AB1234",
                result.get(0).getRegistrationNumber());

        assertEquals("MH14XY5678",
                result.get(1).getRegistrationNumber());

        verify(vehicleRepository).findAll();
    }


    @Test
    void getAllVehicles_shouldReturnEmptyList_whenNoVehiclesExist() {

        // Arrange
        when(vehicleRepository.findAll())
                .thenReturn(List.of());

        // Act
        List<VehicleDto> result =
                vehicleService.getAllVehicles();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(vehicleRepository).findAll();
    }


    // ============================================================
    // GET VEHICLES BY OWNER
    // ============================================================

    @Test
    void getVehiclesByOwner_shouldReturnVehiclesSuccessfully() {

        // Arrange
        when(userService.getUserById(1L))
                .thenReturn(owner);

        when(vehicleRepository.findAllByOwnerId(1L))
                .thenReturn(List.of(vehicle));

        // Act
        List<VehicleDto> result =
                vehicleService.getVehiclesByOwner(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(
                "MH12AB1234",
                result.get(0).getRegistrationNumber()
        );

        verify(userService).getUserById(1L);
        verify(vehicleRepository).findAllByOwnerId(1L);
    }


    @Test
    void getVehiclesByOwner_shouldThrowException_whenUserIsNotOwner() {

        // Arrange
        UserDto serviceAdvisor = UserDto.builder()
                .id(1L)
                .role(Role.Service_Adviser)
                .build();

        when(userService.getUserById(1L))
                .thenReturn(serviceAdvisor);

        // Act & Assert
        assertThrows(
                InvalidVehicleOwnerException.class,
                () -> vehicleService.getVehiclesByOwner(1L)
        );

        verify(userService).getUserById(1L);

        verify(vehicleRepository, never())
                .findAllByOwnerId(anyLong());
    }


    // ============================================================
    // UPDATE VEHICLE
    // ============================================================

    @Test
    void updateVehicle_shouldUpdateSuccessfully_whenRegistrationNumberIsSame() {

        // Arrange
        when(vehicleRepository.findById(10L))
                .thenReturn(Optional.of(vehicle));

        when(userService.getUserById(1L))
                .thenReturn(owner);

        when(vehicleRepository.save(any(Vehicle.class)))
                .thenReturn(vehicle);

        // Act
        VehicleDto result =
                vehicleService.updateVehicle(10L, request);

        // Assert
        assertNotNull(result);

        assertEquals("MH12AB1234",
                result.getRegistrationNumber());

        verify(vehicleRepository).findById(10L);
        verify(userService).getUserById(1L);

        // Registration did not change,
        // so duplicate check should NOT happen.
        verify(vehicleRepository, never())
                .existsByRegistrationNumber(anyString());

        verify(vehicleRepository).save(vehicle);
    }


    @Test
    void updateVehicle_shouldUpdateSuccessfully_whenRegistrationNumberIsChanged() {

        // Arrange
        VehicleRequest updatedRequest =
                VehicleRequest.builder()
                        .ownerId(1L)
                        .registrationNumber("MH12CD9999")
                        .make("Toyota")
                        .model("Innova")
                        .manufacturingYear(2025)
                        .fuelType(FuelType.DIESEL)
                        .build();

        Vehicle updatedVehicle = Vehicle.builder()
                .id(10L)
                .ownerId(1L)
                .registrationNumber("MH12CD9999")
                .make("Toyota")
                .model("Innova")
                .manufacturingYear(2025)
                .fuelType(FuelType.DIESEL)
                .active(true)
                .build();

        when(vehicleRepository.findById(10L))
                .thenReturn(Optional.of(vehicle));

        when(userService.getUserById(1L))
                .thenReturn(owner);

        when(vehicleRepository
                .existsByRegistrationNumber("MH12CD9999"))
                .thenReturn(false);

        when(vehicleRepository.save(any(Vehicle.class)))
                .thenReturn(updatedVehicle);

        // Act
        VehicleDto result =
                vehicleService.updateVehicle(
                        10L,
                        updatedRequest
                );

        // Assert
        assertNotNull(result);

        assertEquals(
                "MH12CD9999",
                result.getRegistrationNumber()
        );

        assertEquals("Innova", result.getModel());

        verify(vehicleRepository)
                .existsByRegistrationNumber("MH12CD9999");

        verify(vehicleRepository).save(vehicle);
    }


    @Test
    void updateVehicle_shouldThrowException_whenNewRegistrationAlreadyExists() {

        // Arrange
        VehicleRequest updatedRequest =
                VehicleRequest.builder()
                        .ownerId(1L)
                        .registrationNumber("MH14XY5678")
                        .make("Toyota")
                        .model("Fortuner")
                        .manufacturingYear(2024)
                        .fuelType(FuelType.DIESEL)
                        .build();

        when(vehicleRepository.findById(10L))
                .thenReturn(Optional.of(vehicle));

        when(userService.getUserById(1L))
                .thenReturn(owner);

        when(vehicleRepository
                .existsByRegistrationNumber("MH14XY5678"))
                .thenReturn(true);

        // Act & Assert
        assertThrows(
                VehicleRegistrationAlreadyExistsException.class,
                () -> vehicleService.updateVehicle(
                        10L,
                        updatedRequest
                )
        );

        verify(vehicleRepository)
                .existsByRegistrationNumber("MH14XY5678");

        verify(vehicleRepository, never())
                .save(any(Vehicle.class));
    }


    @Test
    void updateVehicle_shouldThrowException_whenOwnerIsInvalid() {

        // Arrange
        UserDto serviceAdvisor = UserDto.builder()
                .id(1L)
                .role(Role.Service_Adviser)
                .build();

        when(vehicleRepository.findById(10L))
                .thenReturn(Optional.of(vehicle));

        when(userService.getUserById(1L))
                .thenReturn(serviceAdvisor);

        // Act & Assert
        assertThrows(
                InvalidVehicleOwnerException.class,
                () -> vehicleService.updateVehicle(
                        10L,
                        request
                )
        );

        verify(vehicleRepository, never())
                .save(any(Vehicle.class));
    }


    @Test
    void updateVehicle_shouldThrowException_whenVehicleNotFound() {

        // Arrange
        when(vehicleRepository.findById(10L))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                VehicleNotFoundException.class,
                () -> vehicleService.updateVehicle(
                        10L,
                        request
                )
        );

        verify(userService, never())
                .getUserById(anyLong());

        verify(vehicleRepository, never())
                .save(any(Vehicle.class));
    }


    // ============================================================
    // ACTIVATE VEHICLE
    // ============================================================

    @Test
    void activateVehicle_shouldActivateVehicleSuccessfully() {

        // Arrange
        vehicle.setActive(false);

        when(vehicleRepository.findById(10L))
                .thenReturn(Optional.of(vehicle));

        // Act
        vehicleService.activateVehicle(10L);

        // Assert
        assertTrue(vehicle.isActive());

        verify(vehicleRepository).findById(10L);
        verify(vehicleRepository).save(vehicle);
    }


    @Test
    void activateVehicle_shouldThrowException_whenVehicleNotFound() {

        // Arrange
        when(vehicleRepository.findById(10L))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                VehicleNotFoundException.class,
                () -> vehicleService.activateVehicle(10L)
        );

        verify(vehicleRepository, never())
                .save(any(Vehicle.class));
    }


    // ============================================================
    // DEACTIVATE VEHICLE
    // ============================================================

    @Test
    void deactivateVehicle_shouldDeactivateVehicleSuccessfully() {

        // Arrange
        vehicle.setActive(true);

        when(vehicleRepository.findById(10L))
                .thenReturn(Optional.of(vehicle));

        // Act
        vehicleService.deactivateVehicle(10L);

        // Assert
        assertFalse(vehicle.isActive());

        verify(vehicleRepository).findById(10L);
        verify(vehicleRepository).save(vehicle);
    }


    @Test
    void deactivateVehicle_shouldThrowException_whenVehicleNotFound() {

        // Arrange
        when(vehicleRepository.findById(10L))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                VehicleNotFoundException.class,
                () -> vehicleService.deactivateVehicle(10L)
        );

        verify(vehicleRepository, never())
                .save(any(Vehicle.class));
    }
}

