package com.vehicare.modules.scheduling.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vehicare.modules.scheduling.dto.ServiceAdvisorAvailabilityRequest;
import com.vehicare.modules.scheduling.dto.ServiceAdvisorAvailabilityResponse;
import com.vehicare.modules.scheduling.entity.ServiceAdvisorAvailability;
import com.vehicare.modules.scheduling.exception.InvalidSlotException;
import com.vehicare.modules.scheduling.exception.ServiceAdvisorNotFoundException;
import com.vehicare.modules.scheduling.repository.ServiceAdvisorAvailabilityRepository;
import com.vehicare.modules.user.dto.UserDto;
import com.vehicare.modules.user.entity.Role;
import com.vehicare.modules.user.service.UserService;

@ExtendWith(MockitoExtension.class)
class ServiceAdvisorAvailabilityServiceImplTest {

    @Mock
    private ServiceAdvisorAvailabilityRepository availabilityRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ServiceAdvisorAvailabilityServiceImpl availabilityService;

    private ServiceAdvisorAvailabilityRequest validRequest;

    private ServiceAdvisorAvailability availability;

    private UserDto advisor;

    @BeforeEach
    void setUp() {

        validRequest = ServiceAdvisorAvailabilityRequest.builder()
                .serviceAdvisorId(1L)
                .dayOfWeek(DayOfWeek.MONDAY)
                .availableFrom(LocalTime.of(9, 0))
                .availableTo(LocalTime.of(13, 0))
                .available(true)
                .build();

        availability = ServiceAdvisorAvailability.builder()
                .id(10L)
                .serviceAdvisorId(1L)
                .dayOfWeek(DayOfWeek.MONDAY)
                .availableFrom(LocalTime.of(9, 0))
                .availableTo(LocalTime.of(13, 0))
                .available(true)
                .build();

        advisor = UserDto.builder()
                .id(1L)
                .role(Role.Service_Adviser)
                .build();
    }

    // ---------------------------------------------------------
    // create()
    // ---------------------------------------------------------

    @Test
    void create_shouldCreateAvailabilitySuccessfully() {

        when(userService.getUserById(1L))
                .thenReturn(advisor);

        when(availabilityRepository
                .findByServiceAdvisorIdAndDayOfWeek(
                        1L, DayOfWeek.MONDAY))
                .thenReturn(List.of());

        when(availabilityRepository.save(any(ServiceAdvisorAvailability.class)))
                .thenReturn(availability);

        ServiceAdvisorAvailabilityResponse response =
                availabilityService.create(validRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getServiceAdvisorId()).isEqualTo(1L);
        assertThat(response.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(response.getAvailableFrom())
                .isEqualTo(LocalTime.of(9, 0));
        assertThat(response.getAvailableTo())
                .isEqualTo(LocalTime.of(13, 0));
        assertThat(response.getAvailable()).isTrue();

        verify(userService).getUserById(1L);
        verify(availabilityRepository)
                .findByServiceAdvisorIdAndDayOfWeek(
                        1L, DayOfWeek.MONDAY);
        verify(availabilityRepository)
                .save(any(ServiceAdvisorAvailability.class));
    }

    @Test
    void create_shouldCreateWhenAvailabilityIsFalseWithoutOverlapCheck() {

        validRequest.setAvailable(false);

        when(userService.getUserById(1L))
                .thenReturn(advisor);

        when(availabilityRepository.save(any(ServiceAdvisorAvailability.class)))
                .thenReturn(availability);

        availabilityService.create(validRequest);

        verify(userService).getUserById(1L);

        verify(availabilityRepository, never())
                .findByServiceAdvisorIdAndDayOfWeek(
                        1L, DayOfWeek.MONDAY);

        verify(availabilityRepository)
                .save(any(ServiceAdvisorAvailability.class));
    }

    @Test
    void create_shouldThrowExceptionWhenRequestIsNull() {

        assertThatThrownBy(() ->
                availabilityService.create(null))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage("Availability request is required");

        verifyNoInteractions(userService, availabilityRepository);
    }

    @Test
    void create_shouldThrowExceptionWhenServiceAdvisorIdIsNull() {

        validRequest.setServiceAdvisorId(null);

        assertThatThrownBy(() ->
                availabilityService.create(validRequest))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage("Service advisor ID is required");

        verifyNoInteractions(userService, availabilityRepository);
    }

    @Test
    void create_shouldThrowExceptionWhenDayIsNull() {

        validRequest.setDayOfWeek(null);

        assertThatThrownBy(() ->
                availabilityService.create(validRequest))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage("Day of week is required");

        verifyNoInteractions(userService, availabilityRepository);
    }

    @Test
    void create_shouldThrowExceptionWhenAvailableFromIsNull() {

        validRequest.setAvailableFrom(null);

        assertThatThrownBy(() ->
                availabilityService.create(validRequest))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage("Available from and available to are required");

        verifyNoInteractions(userService, availabilityRepository);
    }

    @Test
    void create_shouldThrowExceptionWhenAvailableToIsNull() {

        validRequest.setAvailableTo(null);

        assertThatThrownBy(() ->
                availabilityService.create(validRequest))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage("Available from and available to are required");

        verifyNoInteractions(userService, availabilityRepository);
    }

    @Test
    void create_shouldThrowExceptionWhenAvailableFromIsAfterAvailableTo() {

        validRequest.setAvailableFrom(LocalTime.of(14, 0));
        validRequest.setAvailableTo(LocalTime.of(13, 0));

        assertThatThrownBy(() ->
                availabilityService.create(validRequest))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage("Available from must be before available to");

        verifyNoInteractions(userService, availabilityRepository);
    }

    @Test
    void create_shouldThrowExceptionWhenAvailabilityStatusIsNull() {

        validRequest.setAvailable(null);

        assertThatThrownBy(() ->
                availabilityService.create(validRequest))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage("Availability status is required");

        verifyNoInteractions(userService, availabilityRepository);
    }

    @Test
    void create_shouldThrowExceptionWhenAdvisorDoesNotExist() {

        when(userService.getUserById(1L))
                .thenThrow(new RuntimeException("User not found"));

        assertThatThrownBy(() ->
                availabilityService.create(validRequest))
                .isInstanceOf(ServiceAdvisorNotFoundException.class)
                .hasMessage("Service advisor not found");

        verify(availabilityRepository, never())
                .save(any(ServiceAdvisorAvailability.class));
    }

    @Test
    void create_shouldThrowExceptionWhenUserIsNotServiceAdvisor() {

        advisor.setRole(Role.Owner);

        when(userService.getUserById(1L))
                .thenReturn(advisor);

        assertThatThrownBy(() ->
                availabilityService.create(validRequest))
                .isInstanceOf(ServiceAdvisorNotFoundException.class)
                .hasMessage("User is not a service advisor");

        verify(availabilityRepository, never())
                .save(any(ServiceAdvisorAvailability.class));
    }

    @Test
    void create_shouldThrowExceptionWhenAvailabilityOverlaps() {

        ServiceAdvisorAvailability existing =
                ServiceAdvisorAvailability.builder()
                        .id(20L)
                        .serviceAdvisorId(1L)
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .availableFrom(LocalTime.of(11, 0))
                        .availableTo(LocalTime.of(15, 0))
                        .available(true)
                        .build();

        when(userService.getUserById(1L))
                .thenReturn(advisor);

        when(availabilityRepository
                .findByServiceAdvisorIdAndDayOfWeek(
                        1L, DayOfWeek.MONDAY))
                .thenReturn(List.of(existing));

        assertThatThrownBy(() ->
                availabilityService.create(validRequest))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage(
                    "Advisor availability overlaps with an existing availability");

        verify(availabilityRepository, never())
                .save(any(ServiceAdvisorAvailability.class));
    }

    // ---------------------------------------------------------
    // getById()
    // ---------------------------------------------------------

    @Test
    void getById_shouldReturnAvailabilitySuccessfully() {

        when(availabilityRepository.findById(10L))
                .thenReturn(Optional.of(availability));

        ServiceAdvisorAvailabilityResponse response =
                availabilityService.getById(10L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getServiceAdvisorId()).isEqualTo(1L);
        assertThat(response.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);

        verify(availabilityRepository).findById(10L);
    }

    @Test
    void getById_shouldThrowExceptionWhenIdIsNull() {

        assertThatThrownBy(() ->
                availabilityService.getById(null))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage("Availability ID is required");

        verifyNoInteractions(availabilityRepository);
    }

    @Test
    void getById_shouldThrowExceptionWhenAvailabilityNotFound() {

        when(availabilityRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                availabilityService.getById(10L))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage("Advisor availability not found");

        verify(availabilityRepository).findById(10L);
    }

    // ---------------------------------------------------------
    // getByAdvisorId()
    // ---------------------------------------------------------

    @Test
    void getByAdvisorId_shouldReturnAvailabilitiesSuccessfully() {

        when(userService.getUserById(1L))
                .thenReturn(advisor);

        when(availabilityRepository.findByServiceAdvisorId(1L))
                .thenReturn(List.of(availability));

        List<ServiceAdvisorAvailabilityResponse> result =
                availabilityService.getByAdvisorId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(10L);
        assertThat(result.get(0).getServiceAdvisorId()).isEqualTo(1L);

        verify(userService).getUserById(1L);
        verify(availabilityRepository).findByServiceAdvisorId(1L);
    }

    @Test
    void getByAdvisorId_shouldReturnEmptyListWhenNoAvailabilityExists() {

        when(userService.getUserById(1L))
                .thenReturn(advisor);

        when(availabilityRepository.findByServiceAdvisorId(1L))
                .thenReturn(List.of());

        List<ServiceAdvisorAvailabilityResponse> result =
                availabilityService.getByAdvisorId(1L);

        assertThat(result).isEmpty();

        verify(availabilityRepository)
                .findByServiceAdvisorId(1L);
    }

    // ---------------------------------------------------------
    // getByAdvisorIdAndDay()
    // ---------------------------------------------------------

    @Test
    void getByAdvisorIdAndDay_shouldReturnAvailabilitiesSuccessfully() {

        when(userService.getUserById(1L))
                .thenReturn(advisor);

        when(availabilityRepository
                .findByServiceAdvisorIdAndDayOfWeek(
                        1L, DayOfWeek.MONDAY))
                .thenReturn(List.of(availability));

        List<ServiceAdvisorAvailabilityResponse> result =
                availabilityService.getByAdvisorIdAndDay(
                        1L, DayOfWeek.MONDAY);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDayOfWeek())
                .isEqualTo(DayOfWeek.MONDAY);

        verify(userService).getUserById(1L);
        verify(availabilityRepository)
                .findByServiceAdvisorIdAndDayOfWeek(
                        1L, DayOfWeek.MONDAY);
    }

    @Test
    void getByAdvisorIdAndDay_shouldThrowExceptionWhenDayIsNull() {

        when(userService.getUserById(1L))
                .thenReturn(advisor);

        assertThatThrownBy(() ->
                availabilityService.getByAdvisorIdAndDay(
                        1L, null))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage("Day of week is required");

        verify(userService).getUserById(1L);

        verify(availabilityRepository, never())
                .findByServiceAdvisorIdAndDayOfWeek(
                        anyLong(), any(DayOfWeek.class));
    }

    // ---------------------------------------------------------
    // update()
    // ---------------------------------------------------------

    @Test
    void update_shouldUpdateAvailabilitySuccessfully() {

        ServiceAdvisorAvailabilityRequest updateRequest =
                ServiceAdvisorAvailabilityRequest.builder()
                        .serviceAdvisorId(1L)
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .availableFrom(LocalTime.of(10, 0))
                        .availableTo(LocalTime.of(14, 0))
                        .available(true)
                        .build();

        when(userService.getUserById(1L))
                .thenReturn(advisor);

        when(availabilityRepository.findById(10L))
                .thenReturn(Optional.of(availability));

        when(availabilityRepository
                .findByServiceAdvisorIdAndDayOfWeek(
                        1L, DayOfWeek.MONDAY))
                .thenReturn(List.of(availability));

        when(availabilityRepository.save(any(ServiceAdvisorAvailability.class)))
                .thenReturn(availability);

        ServiceAdvisorAvailabilityResponse response =
                availabilityService.update(10L, updateRequest);

        assertThat(response).isNotNull();

        assertThat(availability.getAvailableFrom())
                .isEqualTo(LocalTime.of(10, 0));

        assertThat(availability.getAvailableTo())
                .isEqualTo(LocalTime.of(14, 0));

        assertThat(availability.getDayOfWeek())
                .isEqualTo(DayOfWeek.MONDAY);

        verify(availabilityRepository).findById(10L);

        verify(availabilityRepository).findByServiceAdvisorIdAndDayOfWeek(
                1L, DayOfWeek.MONDAY);

        verify(availabilityRepository).save(availability);
    }

    @Test
    void update_shouldThrowExceptionWhenIdIsNull() {

        assertThatThrownBy(() ->
                availabilityService.update(null, validRequest))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage("Availability ID is required");

        verifyNoInteractions(userService, availabilityRepository);
    }

    @Test
    void update_shouldThrowExceptionWhenAvailabilityNotFound() {

        when(userService.getUserById(1L))
                .thenReturn(advisor);

        when(availabilityRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                availabilityService.update(10L, validRequest))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage("Advisor availability not found");

        verify(availabilityRepository).findById(10L);

        verify(availabilityRepository, never())
                .save(any(ServiceAdvisorAvailability.class));
    }

    @Test
    void update_shouldThrowExceptionWhenOverlapExists() {

        ServiceAdvisorAvailability existing =
                ServiceAdvisorAvailability.builder()
                        .id(20L)
                        .serviceAdvisorId(1L)
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .availableFrom(LocalTime.of(11, 0))
                        .availableTo(LocalTime.of(15, 0))
                        .available(true)
                        .build();

        when(userService.getUserById(1L))
                .thenReturn(advisor);

        when(availabilityRepository.findById(10L))
                .thenReturn(Optional.of(availability));

        when(availabilityRepository
                .findByServiceAdvisorIdAndDayOfWeek(
                        1L, DayOfWeek.MONDAY))
                .thenReturn(List.of(existing));

        assertThatThrownBy(() ->
                availabilityService.update(10L, validRequest))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage(
                    "Advisor availability overlaps with an existing availability");

        verify(availabilityRepository, never())
                .save(any(ServiceAdvisorAvailability.class));
    }

    @Test
    void update_shouldAllowUpdatingSameAvailabilityWithoutOverlap() {

        when(userService.getUserById(1L))
                .thenReturn(advisor);

        when(availabilityRepository.findById(10L))
                .thenReturn(Optional.of(availability));

        when(availabilityRepository
                .findByServiceAdvisorIdAndDayOfWeek(
                        1L, DayOfWeek.MONDAY))
                .thenReturn(List.of(availability));

        when(availabilityRepository.save(any(ServiceAdvisorAvailability.class)))
                .thenReturn(availability);

        ServiceAdvisorAvailabilityResponse response =
                availabilityService.update(10L, validRequest);

        assertThat(response).isNotNull();

        verify(availabilityRepository).save(availability);
    }
}