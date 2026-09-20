package com.vehicare.modules.appointment.api;

import java.util.List;

import com.vehicare.modules.appointment.dto.AppointmentAssignAdvisorRequest;
import com.vehicare.modules.appointment.dto.AppointmentCreateRequest;
import com.vehicare.modules.appointment.dto.AppointmentResponse;

public interface AppointmentService {

	AppointmentResponse createAppointment(AppointmentCreateRequest request);

	AppointmentResponse getAppointmentById(Long appointmentId);

	List<AppointmentResponse> getAppointmentsByAdvisor(Long serviceAdvisorId);

	List<AppointmentResponse> getAppointmentsByOwner(Long ownerId);

	List<AppointmentResponse> getAppointmentsByVehicle(Long vehicleId);

	AppointmentResponse assignServiceAdvisor(Long appointmentId, AppointmentAssignAdvisorRequest request);

	AppointmentResponse confirmAppointment(Long appointmentId);

	AppointmentResponse cancelAppointment(Long appointmentId);
}