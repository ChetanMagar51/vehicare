package com.vehicare.modules.scheduling.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.vehicare.modules.scheduling.entity.ServiceSlot;
import com.vehicare.modules.scheduling.entity.ServiceSlotStatus;

@DataJpaTest
class ServiceSlotRepositoryTest {

    @Autowired
    private ServiceSlotRepository serviceSlotRepository;

    @BeforeEach
    void setUp() {

        serviceSlotRepository.deleteAll();

        ServiceSlot slot1 = ServiceSlot.builder()
                .serviceAdvisorId(101L)
                .serviceDate(LocalDate.of(2026, 9, 21))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(9, 30))
                .status(ServiceSlotStatus.AVAILABLE)
                .build();

        ServiceSlot slot2 = ServiceSlot.builder()
                .serviceAdvisorId(101L)
                .serviceDate(LocalDate.of(2026, 9, 21))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(10, 30))
                .status(ServiceSlotStatus.BOOKED)
                .build();

        ServiceSlot slot3 = ServiceSlot.builder()
                .serviceAdvisorId(102L)
                .serviceDate(LocalDate.of(2026, 9, 21))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(9, 30))
                .status(ServiceSlotStatus.BLOCKED)
                .build();

        serviceSlotRepository.saveAll(
                List.of(slot1, slot2, slot3)
        );
    }

    // ----------------------------------------------------
    // save()
    // ----------------------------------------------------

    @Test
    void save_shouldPersistServiceSlot() {

        ServiceSlot slot = ServiceSlot.builder()
                .serviceAdvisorId(103L)
                .serviceDate(LocalDate.of(2026, 9, 22))
                .startTime(LocalTime.of(11, 0))
                .endTime(LocalTime.of(11, 30))
                .status(ServiceSlotStatus.AVAILABLE)
                .build();

        ServiceSlot saved =
                serviceSlotRepository.save(slot);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getServiceAdvisorId()).isEqualTo(103L);
        assertThat(saved.getServiceDate())
                .isEqualTo(LocalDate.of(2026, 9, 22));
        assertThat(saved.getStartTime())
                .isEqualTo(LocalTime.of(11, 0));
        assertThat(saved.getEndTime())
                .isEqualTo(LocalTime.of(11, 30));
        assertThat(saved.getStatus())
                .isEqualTo(ServiceSlotStatus.AVAILABLE);
    }

    // ----------------------------------------------------
    // findById()
    // ----------------------------------------------------

    @Test
    void findById_shouldReturnServiceSlot() {

        ServiceSlot saved =
                serviceSlotRepository.save(
                        ServiceSlot.builder()
                                .serviceAdvisorId(104L)
                                .serviceDate(
                                        LocalDate.of(2026, 9, 22))
                                .startTime(
                                        LocalTime.of(12, 0))
                                .endTime(
                                        LocalTime.of(12, 30))
                                .status(
                                        ServiceSlotStatus.AVAILABLE)
                                .build()
                );

        Optional<ServiceSlot> result =
                serviceSlotRepository.findById(saved.getId());

        assertThat(result).isPresent();

        assertThat(result.get().getServiceAdvisorId())
                .isEqualTo(104L);

        assertThat(result.get().getStatus())
                .isEqualTo(ServiceSlotStatus.AVAILABLE);
    }

    @Test
    void findById_shouldReturnEmptyWhenSlotNotFound() {

        Optional<ServiceSlot> result =
                serviceSlotRepository.findById(999L);

        assertThat(result).isEmpty();
    }

    // ----------------------------------------------------
    // findAll()
    // ----------------------------------------------------

    @Test
    void findAll_shouldReturnAllServiceSlots() {

        List<ServiceSlot> result =
                serviceSlotRepository.findAll();

        assertThat(result).hasSize(3);
    }

    // ----------------------------------------------------
    // delete()
    // ----------------------------------------------------

    @Test
    void delete_shouldDeleteServiceSlot() {

        ServiceSlot saved =
                serviceSlotRepository.save(
                        ServiceSlot.builder()
                                .serviceAdvisorId(105L)
                                .serviceDate(
                                        LocalDate.of(2026, 9, 23))
                                .startTime(
                                        LocalTime.of(14, 0))
                                .endTime(
                                        LocalTime.of(14, 30))
                                .status(
                                        ServiceSlotStatus.BLOCKED)
                                .build()
                );

        Long id = saved.getId();

        serviceSlotRepository.delete(saved);

        Optional<ServiceSlot> result =
                serviceSlotRepository.findById(id);

        assertThat(result).isEmpty();
    }

    // ----------------------------------------------------
    // Unique Constraint
    // ----------------------------------------------------

    @Test
    void save_shouldRejectDuplicateServiceSlot() {

        ServiceSlot duplicate = ServiceSlot.builder()
                .serviceAdvisorId(101L)
                .serviceDate(LocalDate.of(2026, 9, 21))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(9, 30))
                .status(ServiceSlotStatus.AVAILABLE)
                .build();

        assertThrows(
                Exception.class,
                () -> {
                    serviceSlotRepository.saveAndFlush(duplicate);
                }
        );
    }
}