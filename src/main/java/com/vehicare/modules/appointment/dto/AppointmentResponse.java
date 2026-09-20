package com.vehicare.modules.appointment.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.vehicare.modules.appointment.entity.AppointmentStatus;

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
public class AppointmentResponse {

    private Long id;

    private Long vehicleId;

    private Long ownerId;

    private Long serviceAdvisorId;

    private LocalDate serviceDate;

    private LocalTime serviceTime;

    private AppointmentStatus status;
}