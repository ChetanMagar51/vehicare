package com.vehicare.modules.serviceoperations.dto;

import java.time.LocalDateTime;

import com.vehicare.modules.serviceoperations.enumtype.ServiceTaskStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceTaskDto {

    private Long id;

    private Long serviceRecordId;

    private String description;

    private ServiceTaskStatus status;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;
}