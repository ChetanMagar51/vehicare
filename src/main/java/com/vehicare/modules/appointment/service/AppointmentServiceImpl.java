package com.vehicare.modules.appointment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vehicare.modules.appointment.api.AppointmentService;
import com.vehicare.modules.appointment.dto.AppointmentAssignAdvisorRequest;
import com.vehicare.modules.appointment.dto.AppointmentCreateRequest;
import com.vehicare.modules.appointment.dto.AppointmentResponse;
import com.vehicare.modules.appointment.entity.Appointment;
import com.vehicare.modules.appointment.entity.AppointmentStatus;
import com.vehicare.modules.appointment.exception.AppointmentNotFoundException;
import com.vehicare.modules.appointment.repository.AppointmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;

    @Override
    public AppointmentResponse createAppointment(
            AppointmentCreateRequest request) {

        /*
         * Later:
         * 1. Verify vehicle exists.
         * 2. Verify owner exists.
         * 3. Verify vehicle belongs to owner.
         * 4. Verify selected date/time is available.
         */

        Appointment appointment = Appointment.builder()
                .vehicleId(request.getVehicleId())
                .ownerId(request.getOwnerId())
                .serviceDate(request.getServiceDate())
                .serviceTime(request.getServiceTime())
                .status(AppointmentStatus.SCHEDULED)
                .build();

        Appointment savedAppointment =
                appointmentRepository.save(appointment);

        return mapToResponse(savedAppointment);
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(
            Long appointmentId) {

        Appointment appointment =
                appointmentRepository.findById(appointmentId)
                        .orElseThrow(() ->
                                new AppointmentNotFoundException(
                                        "Appointment not found"
                                ));

        return mapToResponse(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByAdvisor(
            Long serviceAdvisorId) {

        return appointmentRepository
                .findByServiceAdvisorId(serviceAdvisorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByOwner(
            Long ownerId) {

        return appointmentRepository
                .findByOwnerId(ownerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByVehicle(
            Long vehicleId) {

        return appointmentRepository
                .findByVehicleId(vehicleId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AppointmentResponse assignServiceAdvisor(
            Long appointmentId,
            AppointmentAssignAdvisorRequest request) {

        Appointment appointment =
                appointmentRepository.findById(appointmentId)
                        .orElseThrow(() ->
                                new AppointmentNotFoundException(
                                        "Appointment not found"
                                ));

        /*
         * Later:
         * Verify that the service advisor exists
         * and has the Service Advisor role.
         */

        appointment.setServiceAdvisorId(
                request.getServiceAdvisorId()
        );

        Appointment updatedAppointment =
                appointmentRepository.save(appointment);

        return mapToResponse(updatedAppointment);
    }

    @Override
    public AppointmentResponse confirmAppointment(
            Long appointmentId) {

        Appointment appointment =
                appointmentRepository.findById(appointmentId)
                        .orElseThrow(() ->
                                new AppointmentNotFoundException(
                                        "Appointment not found"
                                ));

        validateStatus(
                appointment,
                AppointmentStatus.SCHEDULED
        );

        appointment.setStatus(AppointmentStatus.CONFIRMED);

        return mapToResponse(appointment);
    }

    @Override
    public AppointmentResponse cancelAppointment(
            Long appointmentId) {

        Appointment appointment =
                appointmentRepository.findById(appointmentId)
                        .orElseThrow(() ->
                                new AppointmentNotFoundException(
                                        "Appointment not found"
                                ));

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Completed appointment cannot be cancelled"
            );
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Appointment is already cancelled"
            );
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);

        return mapToResponse(appointment);
    }

    private void validateStatus(
            Appointment appointment,
            AppointmentStatus expectedStatus) {

        if (appointment.getStatus() != expectedStatus) {
            throw new IllegalStateException(
                    "Appointment must be in "
                    + expectedStatus
                    + " status"
            );
        }
    }

    private AppointmentResponse mapToResponse(
            Appointment appointment) {

        return AppointmentResponse.builder()
                .id(appointment.getId())
                .vehicleId(appointment.getVehicleId())
                .ownerId(appointment.getOwnerId())
                .serviceAdvisorId(
                        appointment.getServiceAdvisorId()
                )
                .serviceDate(appointment.getServiceDate())
                .serviceTime(appointment.getServiceTime())
                .status(appointment.getStatus())
                .build();
    }
}