package com.vehicare.modules.scheduling.dto;

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
public class AvailableSlotResponse {

    private Long slotId;

    private LocalTime startTime;

    private LocalTime endTime;
}