package com.vehicare.modules.appointment.dto;

import com.vehicare.modules.appointment.entity.AppointmentStatus;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentStatusUpdateRequest {

	@NotNull(message = "Status is required")
	private AppointmentStatus status;
}