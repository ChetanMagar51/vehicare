package com.vehicare.modules.scheduling.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.vehicare.modules.scheduling.entity.ServiceSlot;
import com.vehicare.modules.scheduling.entity.ServiceSlotStatus;

import jakarta.persistence.LockModeType;

public interface ServiceSlotRepository extends JpaRepository<ServiceSlot, Long> {

    List<ServiceSlot> findByServiceAdvisorIdAndServiceDate(
            Long serviceAdvisorId,
            LocalDate serviceDate);

    List<ServiceSlot> findByServiceAdvisorIdAndServiceDateAndStatus(
            Long serviceAdvisorId,
            LocalDate serviceDate,
            ServiceSlotStatus status);

    Optional<ServiceSlot> findByIdAndStatus(
            Long slotId,
            ServiceSlotStatus status);

    boolean existsByServiceAdvisorIdAndServiceDateAndStartTimeAndEndTime(
            Long serviceAdvisorId,
            LocalDate serviceDate,
            LocalTime startTime,
            LocalTime endTime);

    List<ServiceSlot> findByServiceAdvisorIdAndServiceDateBetween(
            Long serviceAdvisorId,
            LocalDate startDate,
            LocalDate endDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ServiceSlot> findWithLockById(Long id);
}
