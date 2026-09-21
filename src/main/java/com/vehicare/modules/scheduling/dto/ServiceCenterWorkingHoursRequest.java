package com.vehicare.modules.scheduling.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

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
public class ServiceCenterWorkingHoursRequest {

    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Opening time is required")
    private LocalTime openingTime;

    @NotNull(message = "Closing time is required")
    private LocalTime closingTime;

    private LocalTime breakStart;

    private LocalTime breakEnd;

    @Builder.Default
    private Boolean closed = false;
}