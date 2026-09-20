package com.vehicare.modules.appointment.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "vehicle_id", nullable = false)
	private Long vehicleId;

	@Column(name = "owner_id", nullable = false)
	private Long ownerId;

	@Column(name = "service_advisor_id")
	private Long serviceAdvisorId;

	@Column(name = "service_date", nullable = false)
	private LocalDate serviceDate;

	@Column(name = "service_time", nullable = false)
	private LocalTime serviceTime;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AppointmentStatus status;
}