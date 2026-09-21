package com.vehicare.modules.scheduling.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.vehicare.modules.scheduling.entity.ServiceCenterWorkingHours;

@DataJpaTest

class ServiceCenterWorkingHoursRepositoryTest {

    @Autowired
    private ServiceCenterWorkingHoursRepository workingHoursRepository;

    @BeforeEach
    void setUp() {

        workingHoursRepository.deleteAll();

        ServiceCenterWorkingHours monday =
                ServiceCenterWorkingHours.builder()
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .openingTime(LocalTime.of(9, 0))
                        .closingTime(LocalTime.of(18, 0))
                        .breakStart(LocalTime.of(13, 0))
                        .breakEnd(LocalTime.of(14, 0))
                        .closed(false)
                        .build();

        ServiceCenterWorkingHours tuesday =
                ServiceCenterWorkingHours.builder()
                        .dayOfWeek(DayOfWeek.TUESDAY)
                        .openingTime(LocalTime.of(9, 0))
                        .closingTime(LocalTime.of(18, 0))
                        .breakStart(LocalTime.of(13, 0))
                        .breakEnd(LocalTime.of(14, 0))
                        .closed(false)
                        .build();

        ServiceCenterWorkingHours sunday =
                ServiceCenterWorkingHours.builder()
                        .dayOfWeek(DayOfWeek.SUNDAY)
                        .openingTime(LocalTime.of(9, 0))
                        .closingTime(LocalTime.of(14, 0))
                        .breakStart(null)
                        .breakEnd(null)
                        .closed(true)
                        .build();

        workingHoursRepository.saveAll(
                List.of(monday, tuesday, sunday)
        );
    }

    // ----------------------------------------------------
    // findByDayOfWeek()
    // ----------------------------------------------------

    @Test
    void findByDayOfWeek_shouldReturnWorkingHours() {

        Optional<ServiceCenterWorkingHours> result =
                workingHoursRepository.findByDayOfWeek(
                        DayOfWeek.MONDAY
                );

        assertThat(result).isPresent();

        ServiceCenterWorkingHours workingHours =
                result.get();

        assertThat(workingHours.getDayOfWeek())
                .isEqualTo(DayOfWeek.MONDAY);

        assertThat(workingHours.getOpeningTime())
                .isEqualTo(LocalTime.of(9, 0));

        assertThat(workingHours.getClosingTime())
                .isEqualTo(LocalTime.of(18, 0));

        assertThat(workingHours.getBreakStart())
                .isEqualTo(LocalTime.of(13, 0));

        assertThat(workingHours.getBreakEnd())
                .isEqualTo(LocalTime.of(14, 0));

        assertThat(workingHours.getClosed())
                .isFalse();
    }

    @Test
    void findByDayOfWeek_shouldReturnEmptyWhenDayNotFound() {

        Optional<ServiceCenterWorkingHours> result =
                workingHoursRepository.findByDayOfWeek(
                        DayOfWeek.WEDNESDAY
                );

        assertThat(result).isEmpty();
    }

    @Test
    void findByDayOfWeek_shouldReturnClosedDay() {

        Optional<ServiceCenterWorkingHours> result =
                workingHoursRepository.findByDayOfWeek(
                        DayOfWeek.SUNDAY
                );

        assertThat(result).isPresent();

        ServiceCenterWorkingHours workingHours =
                result.get();

        assertThat(workingHours.getDayOfWeek())
                .isEqualTo(DayOfWeek.SUNDAY);

        assertThat(workingHours.getClosed())
                .isTrue();

        assertThat(workingHours.getOpeningTime())
                .isEqualTo(LocalTime.of(9, 0));

        assertThat(workingHours.getClosingTime())
                .isEqualTo(LocalTime.of(14, 0));

        assertThat(workingHours.getBreakStart())
                .isNull();

        assertThat(workingHours.getBreakEnd())
                .isNull();
    }
}