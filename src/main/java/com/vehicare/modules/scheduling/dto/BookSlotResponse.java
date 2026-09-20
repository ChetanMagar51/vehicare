package com.vehicare.modules.scheduling.dto;

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
public class BookSlotResponse {

    private Long slotId;

    private Long serviceAdvisorId;

    private LocalDate serviceDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private String message;
}