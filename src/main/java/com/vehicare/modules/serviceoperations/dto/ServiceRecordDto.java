package com.vehicare.modules.serviceoperations.dto;

import java.time.LocalDateTime;

import com.vehicare.modules.serviceoperations.enumtype.ServiceStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRecordDto {

    private Long id;

    private Long appointmentId;

    private Long vehicleId;

    private Long ownerId;

    private Long serviceAdvisorId;

    private String description;

    private ServiceStatus status;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}