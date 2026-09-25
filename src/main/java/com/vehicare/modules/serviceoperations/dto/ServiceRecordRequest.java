package com.vehicare.modules.serviceoperations.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRecordRequest {

    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;

    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    @NotNull(message = "Owner ID is required")
    private Long ownerId;

    @NotNull(message = "Service advisor ID is required")
    private Long serviceAdvisorId;

    @NotBlank(message = "Service description is required")
    private String description;
}