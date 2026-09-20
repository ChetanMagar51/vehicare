package com.vehicare.modules.scheduling.dto;

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
public class BookSlotRequest {

    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    @NotNull(message = "Service Advisor ID is required")
    private Long serviceAdvisorId;

    @NotNull(message = "Service Slot ID is required")
    private Long serviceSlotId;
}