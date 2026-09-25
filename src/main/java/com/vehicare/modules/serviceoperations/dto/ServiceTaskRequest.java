package com.vehicare.modules.serviceoperations.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceTaskRequest {

    @NotBlank(message = "Task description is required")
    private String description;
}