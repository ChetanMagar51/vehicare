package com.vehicare.modules.serviceadvisor.dto;

import java.time.LocalDate;
import java.time.LocalTime;

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
public class ServiceAdvisorScheduleResponse {

    private Long scheduleId;

    private Long vehicleId;

    private String registrationNumber;

    private Long ownerId;

    private String ownerName;

    private LocalDate serviceDate;

    private LocalTime serviceTime;

    private String status;
}