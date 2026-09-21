package com.vehicare.modules.scheduling.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.vehicare.modules.scheduling.entity.ServiceAdvisorAvailability;

@DataJpaTest
class ServiceAdvisorAvailabilityRepositoryTest {

    @Autowired
    private ServiceAdvisorAvailabilityRepository availabilityRepository;

    @BeforeEach
    void setUp() {

        availabilityRepository.deleteAll();

        ServiceAdvisorAvailability availability1 =
                new ServiceAdvisorAvailability();

        availability1.setServiceAdvisorId(101L);
        availability1.setDayOfWeek(DayOfWeek.MONDAY);
        availability1.setAvailableFrom(LocalTime.of(9, 0));
        availability1.setAvailableTo(LocalTime.of(17, 0));
        availability1.setAvailable(true);

        ServiceAdvisorAvailability availability2 =
                new ServiceAdvisorAvailability();

        availability2.setServiceAdvisorId(101L);
        availability2.setDayOfWeek(DayOfWeek.WEDNESDAY);
        availability2.setAvailableFrom(LocalTime.of(10, 0));
        availability2.setAvailableTo(LocalTime.of(18, 0));
        availability2.setAvailable(true);

        ServiceAdvisorAvailability availability3 =
                new ServiceAdvisorAvailability();

        availability3.setServiceAdvisorId(102L);
        availability3.setDayOfWeek(DayOfWeek.MONDAY);
        availability3.setAvailableFrom(LocalTime.of(9, 0));
        availability3.setAvailableTo(LocalTime.of(13, 0));
        availability3.setAvailable(true);

        availabilityRepository.saveAll(
                List.of(
                        availability1,
                        availability2,
                        availability3
                )
        );
    }

    // ----------------------------------------------------
    // findByDayOfWeek()
    // ----------------------------------------------------

    @Test
    void findByDayOfWeek_shouldReturnMatchingAvailabilities() {

        List<ServiceAdvisorAvailability> result =
                availabilityRepository.findByDayOfWeek(
                        DayOfWeek.MONDAY
                );

        assertThat(result).hasSize(2);

        assertThat(result)
                .allMatch(availability ->
                        availability.getDayOfWeek()
                                == DayOfWeek.MONDAY);
    }

    @Test
    void findByDayOfWeek_shouldReturnEmptyListWhenNoMatch() {

        List<ServiceAdvisorAvailability> result =
                availabilityRepository.findByDayOfWeek(
                        DayOfWeek.FRIDAY
                );

        assertThat(result).isEmpty();
    }

    // ----------------------------------------------------
    // findByServiceAdvisorId()
    // ----------------------------------------------------

    @Test
    void findByServiceAdvisorId_shouldReturnMatchingAvailabilities() {

        List<ServiceAdvisorAvailability> result =
                availabilityRepository.findByServiceAdvisorId(101L);

        assertThat(result).hasSize(2);

        assertThat(result)
                .allMatch(availability ->
                        availability.getServiceAdvisorId()
                                .equals(101L));
    }

    @Test
    void findByServiceAdvisorId_shouldReturnEmptyListWhenNoMatch() {

        List<ServiceAdvisorAvailability> result =
                availabilityRepository.findByServiceAdvisorId(999L);

        assertThat(result).isEmpty();
    }

    // ----------------------------------------------------
    // findByServiceAdvisorIdAndDayOfWeek()
    // ----------------------------------------------------

    @Test
    void findByServiceAdvisorIdAndDayOfWeek_shouldReturnMatchingAvailability() {

        List<ServiceAdvisorAvailability> result =
                availabilityRepository
                        .findByServiceAdvisorIdAndDayOfWeek(
                                101L,
                                DayOfWeek.MONDAY
                        );

        assertThat(result).hasSize(1);

        assertThat(result.get(0).getServiceAdvisorId())
                .isEqualTo(101L);

        assertThat(result.get(0).getDayOfWeek())
                .isEqualTo(DayOfWeek.MONDAY);

        assertThat(result.get(0).getAvailableFrom())
                .isEqualTo(LocalTime.of(9, 0));

        assertThat(result.get(0).getAvailableTo())
                .isEqualTo(LocalTime.of(17, 0));

        assertThat(result.get(0).getAvailable())
                .isTrue();
    }

    @Test
    void findByServiceAdvisorIdAndDayOfWeek_shouldReturnEmptyListWhenNoMatch() {

        List<ServiceAdvisorAvailability> result =
                availabilityRepository
                        .findByServiceAdvisorIdAndDayOfWeek(
                                101L,
                                DayOfWeek.FRIDAY
                        );

        assertThat(result).isEmpty();
    }

    @Test
    void findByServiceAdvisorIdAndDayOfWeek_shouldReturnEmptyListForWrongAdvisor() {

        List<ServiceAdvisorAvailability> result =
                availabilityRepository
                        .findByServiceAdvisorIdAndDayOfWeek(
                                999L,
                                DayOfWeek.MONDAY
                        );

        assertThat(result).isEmpty();
    }
}