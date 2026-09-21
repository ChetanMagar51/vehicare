package com.vehicare.modules.scheduling.api;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import com.vehicare.modules.scheduling.dto.AvailableSlotResponse;

public interface SchedulingService {


	List<AvailableSlotResponse> getAvailableSlots(Long serviceAdvisorId, LocalDate date);

	void generateSlotsForAdvisor(Long serviceAdvisorId, LocalDate startDate, LocalDate endDate);

	 void generateMonthlySlots(YearMonth month);
}