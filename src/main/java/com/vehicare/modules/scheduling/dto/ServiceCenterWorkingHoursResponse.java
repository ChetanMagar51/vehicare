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
public class ServiceCenterWorkingHoursResponse {

    private Long id;
    private DayOfWeek dayOfWeek;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private LocalTime breakStart;
    private LocalTime breakEnd;
    private Boolean closed;
}