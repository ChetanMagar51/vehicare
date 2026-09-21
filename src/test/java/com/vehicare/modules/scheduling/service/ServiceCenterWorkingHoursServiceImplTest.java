package com.vehicare.modules.scheduling.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

import com.vehicare.modules.scheduling.dto.ServiceCenterWorkingHoursRequest;
import com.vehicare.modules.scheduling.dto.ServiceCenterWorkingHoursResponse;
import com.vehicare.modules.scheduling.entity.ServiceCenterWorkingHours;
import com.vehicare.modules.scheduling.exception.InvalidSlotException;
import com.vehicare.modules.scheduling.repository.ServiceCenterWorkingHoursRepository;

@ExtendWith(MockitoExtension.class)
class ServiceCenterWorkingHoursServiceImplTest {

    @Mock
    private ServiceCenterWorkingHoursRepository workingHoursRepository;

    @InjectMocks
    private ServiceCenterWorkingHoursServiceImpl workingHoursService;

    private ServiceCenterWorkingHoursRequest validRequest;

    private ServiceCenterWorkingHours workingHours;

    @BeforeEach
    void setUp() {

        validRequest = ServiceCenterWorkingHoursRequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openingTime(LocalTime.of(9, 0))
                .closingTime(LocalTime.of(18, 0))
                .breakStart(LocalTime.of(13, 0))
                .breakEnd(LocalTime.of(14, 0))
                .closed(false)
                .build();

        workingHours = ServiceCenterWorkingHours.builder()
                .id(1L)
                .dayOfWeek(DayOfWeek.MONDAY)
                .openingTime(LocalTime.of(9, 0))
                .closingTime(LocalTime.of(18, 0))
                .breakStart(LocalTime.of(13, 0))
                .breakEnd(LocalTime.of(14, 0))
                .closed(false)
                .build();
    }

    // ---------------------------------------------------------
    // create()
    // ---------------------------------------------------------

    @Test
    void create_shouldCreateWorkingHoursSuccessfully() {

        when(workingHoursRepository.findByDayOfWeek(DayOfWeek.MONDAY))
                .thenReturn(Optional.empty());

        when(workingHoursRepository.save(any(ServiceCenterWorkingHours.class)))
                .thenReturn(workingHours);

        ServiceCenterWorkingHoursResponse response =
                workingHoursService.create(validRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(response.getOpeningTime()).isEqualTo(LocalTime.of(9, 0));
        assertThat(response.getClosingTime()).isEqualTo(LocalTime.of(18, 0));
        assertThat(response.getBreakStart()).isEqualTo(LocalTime.of(13, 0));
        assertThat(response.getBreakEnd()).isEqualTo(LocalTime.of(14, 0));
        assertThat(response.getClosed()).isFalse();

        verify(workingHoursRepository).findByDayOfWeek(DayOfWeek.MONDAY);
        verify(workingHoursRepository).save(any(ServiceCenterWorkingHours.class));
    }

    @Test
    void create_shouldThrowExceptionWhenDayAlreadyConfigured() {

        when(workingHoursRepository.findByDayOfWeek(DayOfWeek.MONDAY))
                .thenReturn(Optional.of(workingHours));

        assertThatThrownBy(() -> workingHoursService.create(validRequest))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage("Working hours already configured for MONDAY");

        verify(workingHoursRepository, never())
                .save(any(ServiceCenterWorkingHours.class));
    }

    @Test
    void create_shouldThrowExceptionWhenRequestIsNull() {

        assertThatThrownBy(() -> workingHoursService.create(null))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage("Working hours request is required");

        verifyNoInteractions(workingHoursRepository);
    }

    @Test
    void create_shouldThrowExceptionWhenDayIsNull() {

        validRequest.setDayOfWeek(null);

        assertThatThrownBy(() -> workingHoursService.create(validRequest))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage("Day of week is required");

        verifyNoInteractions(workingHoursRepository);
    }

    @Test
    void create_shouldThrowExceptionWhenOpeningTimeIsNull() {

        validRequest.setOpeningTime(null);

        assertThatThrownBy(() -> workingHoursService.create(validRequest))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage("Opening time and closing time are required");

        verifyNoInteractions(workingHoursRepository);
    }

    @Test
    void create_shouldThrowExceptionWhenClosingTimeIsNull() {

        validRequest.setClosingTime(null);

        assertThatThrownBy(() -> workingHoursService.create(validRequest))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage("Opening time and closing time are required");

        verifyNoInteractions(workingHoursRepository);
    }

    @Test
    void create_shouldThrowExceptionWhenOpeningTimeIsAfterClosingTime() {

        validRequest.setOpeningTime(LocalTime.of(18, 0));
        validRequest.setClosingTime(LocalTime.of(9, 0));

        assertThatThrownBy(() -> workingHoursService.create(validRequest))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage("Opening time must be before closing time");

        verifyNoInteractions(workingHoursRepository);
    }

    @Test
    void create_shouldThrowExceptionWhenOnlyBreakStartIsProvided() {

        validRequest.setBreakStart(LocalTime.of(13, 0));
        validRequest.setBreakEnd(null);

        assertThatThrownBy(() -> workingHoursService.create(validRequest))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage("Both break start and break end are required");

        verifyNoInteractions(workingHoursRepository);
    }

    @Test
    void create_shouldThrowExceptionWhenOnlyBreakEndIsProvided() {

        validRequest.setBreakStart(null);
        validRequest.setBreakEnd(LocalTime.of(14, 0));

        assertThatThrownBy(() -> workingHoursService.create(validRequest))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage("Both break start and break end are required");

        verifyNoInteractions(workingHoursRepository);
    }

    @Test
    void create_shouldThrowExceptionWhenBreakStartIsAfterBreakEnd() {

        validRequest.setBreakStart(LocalTime.of(15, 0));
        validRequest.setBreakEnd(LocalTime.of(14, 0));

        assertThatThrownBy(() -> workingHoursService.create(validRequest))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage("Break start time must be before break end time");

        verifyNoInteractions(workingHoursRepository);
    }

    @Test
    void create_shouldThrowExceptionWhenBreakIsOutsideWorkingHours() {

        validRequest.setBreakStart(LocalTime.of(8, 0));
        validRequest.setBreakEnd(LocalTime.of(10, 0));

        assertThatThrownBy(() -> workingHoursService.create(validRequest))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage("Break must be within center working hours");

        verifyNoInteractions(workingHoursRepository);
    }

    // ---------------------------------------------------------
    // getAll()
    // ---------------------------------------------------------

    @Test
    void getAll_shouldReturnWorkingHoursList() {

        ServiceCenterWorkingHours tuesday =
                ServiceCenterWorkingHours.builder()
                        .id(2L)
                        .dayOfWeek(DayOfWeek.TUESDAY)
                        .openingTime(LocalTime.of(9, 0))
                        .closingTime(LocalTime.of(18, 0))
                        .breakStart(LocalTime.of(13, 0))
                        .breakEnd(LocalTime.of(14, 0))
                        .closed(false)
                        .build();

        when(workingHoursRepository.findAll())
                .thenReturn(List.of(workingHours, tuesday));

        List<ServiceCenterWorkingHoursResponse> result =
                workingHoursService.getAll();

        assertThat(result).hasSize(2);

        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);

        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getDayOfWeek()).isEqualTo(DayOfWeek.TUESDAY);

        verify(workingHoursRepository).findAll();
    }

    @Test
    void getAll_shouldReturnEmptyListWhenNoWorkingHoursExist() {

        when(workingHoursRepository.findAll())
                .thenReturn(List.of());

        List<ServiceCenterWorkingHoursResponse> result =
                workingHoursService.getAll();

        assertThat(result).isEmpty();

        verify(workingHoursRepository).findAll();
    }

    // ---------------------------------------------------------
    // getByDay()
    // ---------------------------------------------------------

    @Test
    void getByDay_shouldReturnWorkingHoursSuccessfully() {

        when(workingHoursRepository.findByDayOfWeek(DayOfWeek.MONDAY))
                .thenReturn(Optional.of(workingHours));

        ServiceCenterWorkingHoursResponse response =
                workingHoursService.getByDay(DayOfWeek.MONDAY);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(response.getOpeningTime()).isEqualTo(LocalTime.of(9, 0));
        assertThat(response.getClosingTime()).isEqualTo(LocalTime.of(18, 0));

        verify(workingHoursRepository)
                .findByDayOfWeek(DayOfWeek.MONDAY);
    }

    @Test
    void getByDay_shouldThrowExceptionWhenDayIsNull() {

        assertThatThrownBy(() -> workingHoursService.getByDay(null))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage("Day of week is required");

        verifyNoInteractions(workingHoursRepository);
    }

    @Test
    void getByDay_shouldThrowExceptionWhenDayNotFound() {

        when(workingHoursRepository.findByDayOfWeek(DayOfWeek.WEDNESDAY))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> workingHoursService.getByDay(DayOfWeek.WEDNESDAY))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage("Working hours not found for WEDNESDAY");

        verify(workingHoursRepository)
                .findByDayOfWeek(DayOfWeek.WEDNESDAY);
    }

    // ---------------------------------------------------------
    // update()
    // ---------------------------------------------------------

    @Test
    void update_shouldUpdateWorkingHoursSuccessfully() {

        ServiceCenterWorkingHoursRequest updateRequest =
                ServiceCenterWorkingHoursRequest.builder()
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .openingTime(LocalTime.of(8, 30))
                        .closingTime(LocalTime.of(19, 0))
                        .breakStart(LocalTime.of(13, 0))
                        .breakEnd(LocalTime.of(14, 0))
                        .closed(false)
                        .build();

        when(workingHoursRepository.findByDayOfWeek(DayOfWeek.MONDAY))
                .thenReturn(Optional.of(workingHours));

        when(workingHoursRepository.save(any(ServiceCenterWorkingHours.class)))
                .thenReturn(workingHours);

        ServiceCenterWorkingHoursResponse response =
                workingHoursService.update(DayOfWeek.MONDAY, updateRequest);

        assertThat(response).isNotNull();

        assertThat(workingHours.getOpeningTime())
                .isEqualTo(LocalTime.of(8, 30));

        assertThat(workingHours.getClosingTime())
                .isEqualTo(LocalTime.of(19, 0));

        assertThat(workingHours.getBreakStart())
                .isEqualTo(LocalTime.of(13, 0));

        assertThat(workingHours.getBreakEnd())
                .isEqualTo(LocalTime.of(14, 0));

        verify(workingHoursRepository)
                .findByDayOfWeek(DayOfWeek.MONDAY);

        verify(workingHoursRepository)
                .save(workingHours);
    }

    @Test
    void update_shouldThrowExceptionWhenDayIsNull() {

        assertThatThrownBy(() ->
                workingHoursService.update(null, validRequest))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage("Day of week is required");

        verifyNoInteractions(workingHoursRepository);
    }

    @Test
    void update_shouldThrowExceptionWhenRequestIsNull() {

        assertThatThrownBy(() ->
                workingHoursService.update(DayOfWeek.MONDAY, null))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage("Working hours request is required");

        verifyNoInteractions(workingHoursRepository);
    }

    @Test
    void update_shouldThrowExceptionWhenDayNotFound() {

        when(workingHoursRepository.findByDayOfWeek(DayOfWeek.MONDAY))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                workingHoursService.update(DayOfWeek.MONDAY, validRequest))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage("Working hours not found for MONDAY");

        verify(workingHoursRepository)
                .findByDayOfWeek(DayOfWeek.MONDAY);

        verify(workingHoursRepository, never())
                .save(any(ServiceCenterWorkingHours.class));
    }

    @Test
    void update_shouldThrowExceptionWhenDayIsChanged() {

        validRequest.setDayOfWeek(DayOfWeek.TUESDAY);

        when(workingHoursRepository.findByDayOfWeek(DayOfWeek.MONDAY))
                .thenReturn(Optional.of(workingHours));

        assertThatThrownBy(() ->
                workingHoursService.update(DayOfWeek.MONDAY, validRequest))
                .isInstanceOf(InvalidSlotException.class)
                .hasMessage("Day of week cannot be changed during update");

        verify(workingHoursRepository)
                .findByDayOfWeek(DayOfWeek.MONDAY);

        verify(workingHoursRepository, never())
                .save(any(ServiceCenterWorkingHours.class));
    }
}