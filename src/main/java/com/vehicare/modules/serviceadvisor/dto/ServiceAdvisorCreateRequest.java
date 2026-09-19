package com.vehicare.modules.serviceadvisor.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class ServiceAdvisorCreateRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    private String specialization;

    @Min(value = 0, message = "Experience cannot be negative")
    private Integer experience;

    private Boolean available;
}