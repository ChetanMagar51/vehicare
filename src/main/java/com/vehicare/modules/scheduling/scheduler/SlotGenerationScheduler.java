package com.vehicare.modules.scheduling.scheduler;

import java.time.YearMonth;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vehicare.modules.scheduling.api.SchedulingService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SlotGenerationScheduler {

    private final SchedulingService schedulingService;

    @Scheduled(cron = "0 0 0 1 * *")
    public void generateMonthlySlots() {

        YearMonth targetMonth = YearMonth.now().plusMonths(2);

        schedulingService.generateMonthlySlots(targetMonth);
    }
}