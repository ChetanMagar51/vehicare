package com.vehicare.modules.scheduling.dto;

import java.time.DayOfWeek;
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
public class ServiceAdvisorAvailabilityResponse {

    private Long id;
    private Long serviceAdvisorId;
    private DayOfWeek dayOfWeek;
    private LocalTime availableFrom;
    private LocalTime availableTo;
    private Boolean available;
}