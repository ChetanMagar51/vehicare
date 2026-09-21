package com.vehicare.modules.scheduling.scheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import java.time.YearMonth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vehicare.modules.scheduling.api.SchedulingService;

@ExtendWith(MockitoExtension.class)
class SlotGenerationSchedulerTest {

	@Mock
	private SchedulingService schedulingService;

	@InjectMocks
	private SlotGenerationScheduler slotGenerationScheduler;

	@Test
	void generateMonthlySlots_shouldGenerateSlotsForMonthTwoMonthsAhead() {

		YearMonth expectedMonth = YearMonth.now().plusMonths(2);

		slotGenerationScheduler.generateMonthlySlots();

		ArgumentCaptor<YearMonth> captor = ArgumentCaptor.forClass(YearMonth.class);

		verify(schedulingService).generateMonthlySlots(captor.capture());

		YearMonth actualMonth = captor.getValue();

		assertEquals(expectedMonth, actualMonth);
	}
}