package com.vehicare.modules.appointment.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentCreateRequest {

	@NotNull(message = "Vehicle ID is required")
	private Long vehicleId;

	@NotNull(message = "Owner ID is required")
	private Long ownerId;

	@NotNull(message = "Service date is required")
	@FutureOrPresent(message = "Service date cannot be in the past")
	private LocalDate serviceDate;

	@NotNull(message = "Service time is required")
	private LocalTime serviceTime;
}