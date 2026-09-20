package com.vehicare.modules.appointment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vehicare.modules.appointment.dto.AppointmentAssignAdvisorRequest;
import com.vehicare.modules.appointment.dto.AppointmentCreateRequest;
import com.vehicare.modules.appointment.dto.AppointmentResponse;
import com.vehicare.modules.appointment.entity.Appointment;
import com.vehicare.modules.appointment.entity.AppointmentStatus;
import com.vehicare.modules.appointment.exception.AppointmentNotFoundException;
import com.vehicare.modules.appointment.repository.AppointmentRepository;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private Appointment appointment;

    @BeforeEach
    void setUp() {

        appointment = Appointment.builder()
                .id(1L)
                .vehicleId(301L)
                .ownerId(201L)
                .serviceAdvisorId(101L)
                .serviceDate(LocalDate.of(2026, 9, 21))
                .serviceTime(LocalTime.of(10, 0))
                .status(AppointmentStatus.SCHEDULED)
                .build();
    }

    // ----------------------------------------------------
    // createAppointment()
    // ----------------------------------------------------

    @Test
    void createAppointment_shouldCreateAppointmentSuccessfully() {

        AppointmentCreateRequest request =
                new AppointmentCreateRequest();

        request.setVehicleId(301L);
        request.setOwnerId(201L);
        request.setServiceDate(LocalDate.of(2026, 9, 21));
        request.setServiceTime(LocalTime.of(10, 0));

        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(appointment);

        AppointmentResponse response =
                appointmentService.createAppointment(request);

        assertEquals(1L, response.getId());
        assertEquals(301L, response.getVehicleId());
        assertEquals(201L, response.getOwnerId());
        assertEquals(
                LocalDate.of(2026, 9, 21),
                response.getServiceDate()
        );
        assertEquals(
                LocalTime.of(10, 0),
                response.getServiceTime()
        );
        assertEquals(
                AppointmentStatus.SCHEDULED,
                response.getStatus()
        );

        verify(appointmentRepository).save(any(Appointment.class));
    }

    // ----------------------------------------------------
    // getAppointmentById()
    // ----------------------------------------------------

    @Test
    void getAppointmentById_shouldReturnAppointmentSuccessfully() {

        when(appointmentRepository.findById(1L))
                .thenReturn(Optional.of(appointment));

        AppointmentResponse response =
                appointmentService.getAppointmentById(1L);

        assertEquals(1L, response.getId());
        assertEquals(301L, response.getVehicleId());
        assertEquals(201L, response.getOwnerId());
        assertEquals(101L, response.getServiceAdvisorId());
        assertEquals(AppointmentStatus.SCHEDULED, response.getStatus());

        verify(appointmentRepository).findById(1L);
    }

    @Test
    void getAppointmentById_shouldThrowExceptionWhenNotFound() {

        when(appointmentRepository.findById(1L))
                .thenReturn(Optional.empty());

        AppointmentNotFoundException exception =
                assertThrows(
                        AppointmentNotFoundException.class,
                        () -> appointmentService.getAppointmentById(1L)
                );

        assertEquals(
                "Appointment not found",
                exception.getMessage()
        );

        verify(appointmentRepository).findById(1L);
    }

    // ----------------------------------------------------
    // getAppointmentsByAdvisor()
    // ----------------------------------------------------

    @Test
    void getAppointmentsByAdvisor_shouldReturnAppointments() {

        Appointment appointment2 = Appointment.builder()
                .id(2L)
                .vehicleId(302L)
                .ownerId(202L)
                .serviceAdvisorId(101L)
                .serviceDate(LocalDate.of(2026, 9, 22))
                .serviceTime(LocalTime.of(11, 0))
                .status(AppointmentStatus.SCHEDULED)
                .build();

        when(appointmentRepository.findByServiceAdvisorId(101L))
                .thenReturn(List.of(appointment, appointment2));

        List<AppointmentResponse> response =
                appointmentService.getAppointmentsByAdvisor(101L);

        assertEquals(2, response.size());

        assertEquals(1L, response.get(0).getId());
        assertEquals(2L, response.get(1).getId());

        verify(appointmentRepository)
                .findByServiceAdvisorId(101L);
    }

    @Test
    void getAppointmentsByAdvisor_shouldReturnEmptyList() {

        when(appointmentRepository.findByServiceAdvisorId(999L))
                .thenReturn(List.of());

        List<AppointmentResponse> response =
                appointmentService.getAppointmentsByAdvisor(999L);

        assertEquals(0, response.size());

        verify(appointmentRepository)
                .findByServiceAdvisorId(999L);
    }

    // ----------------------------------------------------
    // getAppointmentsByOwner()
    // ----------------------------------------------------

    @Test
    void getAppointmentsByOwner_shouldReturnAppointments() {

        when(appointmentRepository.findByOwnerId(201L))
                .thenReturn(List.of(appointment));

        List<AppointmentResponse> response =
                appointmentService.getAppointmentsByOwner(201L);

        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getId());
        assertEquals(201L, response.get(0).getOwnerId());

        verify(appointmentRepository)
                .findByOwnerId(201L);
    }

    @Test
    void getAppointmentsByOwner_shouldReturnEmptyList() {

        when(appointmentRepository.findByOwnerId(999L))
                .thenReturn(List.of());

        List<AppointmentResponse> response =
                appointmentService.getAppointmentsByOwner(999L);

        assertEquals(0, response.size());

        verify(appointmentRepository)
                .findByOwnerId(999L);
    }

    // ----------------------------------------------------
    // getAppointmentsByVehicle()
    // ----------------------------------------------------

    @Test
    void getAppointmentsByVehicle_shouldReturnAppointments() {

        when(appointmentRepository.findByVehicleId(301L))
                .thenReturn(List.of(appointment));

        List<AppointmentResponse> response =
                appointmentService.getAppointmentsByVehicle(301L);

        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getId());
        assertEquals(301L, response.get(0).getVehicleId());

        verify(appointmentRepository)
                .findByVehicleId(301L);
    }

    @Test
    void getAppointmentsByVehicle_shouldReturnEmptyList() {

        when(appointmentRepository.findByVehicleId(999L))
                .thenReturn(List.of());

        List<AppointmentResponse> response =
                appointmentService.getAppointmentsByVehicle(999L);

        assertEquals(0, response.size());

        verify(appointmentRepository)
                .findByVehicleId(999L);
    }

    // ----------------------------------------------------
    // assignServiceAdvisor()
    // ----------------------------------------------------

    @Test
    void assignServiceAdvisor_shouldAssignAdvisorSuccessfully() {

        AppointmentAssignAdvisorRequest request =
                new AppointmentAssignAdvisorRequest();

        request.setServiceAdvisorId(102L);

        when(appointmentRepository.findById(1L))
                .thenReturn(Optional.of(appointment));

        when(appointmentRepository.save(appointment))
                .thenReturn(appointment);

        AppointmentResponse response =
                appointmentService.assignServiceAdvisor(1L, request);

        assertEquals(102L, response.getServiceAdvisorId());

        verify(appointmentRepository).findById(1L);
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void assignServiceAdvisor_shouldThrowExceptionWhenAppointmentNotFound() {

        AppointmentAssignAdvisorRequest request =
                new AppointmentAssignAdvisorRequest();

        request.setServiceAdvisorId(102L);

        when(appointmentRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                AppointmentNotFoundException.class,
                () -> appointmentService.assignServiceAdvisor(999L, request)
        );

        verify(appointmentRepository).findById(999L);
        verify(appointmentRepository, never())
                .save(any(Appointment.class));
    }

    // ----------------------------------------------------
    // confirmAppointment()
    // ----------------------------------------------------

    @Test
    void confirmAppointment_shouldConfirmScheduledAppointment() {

        appointment.setStatus(AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findById(1L))
                .thenReturn(Optional.of(appointment));

        AppointmentResponse response =
                appointmentService.confirmAppointment(1L);

        assertEquals(
                AppointmentStatus.CONFIRMED,
                response.getStatus()
        );

        verify(appointmentRepository).findById(1L);
    }

    @Test
    void confirmAppointment_shouldThrowExceptionForWrongStatus() {

        appointment.setStatus(AppointmentStatus.CANCELLED);

        when(appointmentRepository.findById(1L))
                .thenReturn(Optional.of(appointment));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> appointmentService.confirmAppointment(1L)
                );

        assertEquals(
                "Appointment must be in SCHEDULED status",
                exception.getMessage()
        );

        verify(appointmentRepository).findById(1L);
    }

    @Test
    void confirmAppointment_shouldThrowExceptionWhenNotFound() {

        when(appointmentRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                AppointmentNotFoundException.class,
                () -> appointmentService.confirmAppointment(999L)
        );

        verify(appointmentRepository).findById(999L);
    }

    // ----------------------------------------------------
    // cancelAppointment()
    // ----------------------------------------------------

    @Test
    void cancelAppointment_shouldCancelAppointmentSuccessfully() {

        appointment.setStatus(AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findById(1L))
                .thenReturn(Optional.of(appointment));

        AppointmentResponse response =
                appointmentService.cancelAppointment(1L);

        assertEquals(
                AppointmentStatus.CANCELLED,
                response.getStatus()
        );

        verify(appointmentRepository).findById(1L);
    }

    @Test
    void cancelAppointment_shouldThrowExceptionWhenCompleted() {

        appointment.setStatus(AppointmentStatus.COMPLETED);

        when(appointmentRepository.findById(1L))
                .thenReturn(Optional.of(appointment));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> appointmentService.cancelAppointment(1L)
                );

        assertEquals(
                "Completed appointment cannot be cancelled",
                exception.getMessage()
        );

        verify(appointmentRepository).findById(1L);
    }

    @Test
    void cancelAppointment_shouldThrowExceptionWhenAlreadyCancelled() {

        appointment.setStatus(AppointmentStatus.CANCELLED);

        when(appointmentRepository.findById(1L))
                .thenReturn(Optional.of(appointment));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> appointmentService.cancelAppointment(1L)
                );

        assertEquals(
                "Appointment is already cancelled",
                exception.getMessage()
        );

        verify(appointmentRepository).findById(1L);
    }

    @Test
    void cancelAppointment_shouldThrowExceptionWhenNotFound() {

        when(appointmentRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                AppointmentNotFoundException.class,
                () -> appointmentService.cancelAppointment(999L)
        );

        verify(appointmentRepository).findById(999L);
    }
}