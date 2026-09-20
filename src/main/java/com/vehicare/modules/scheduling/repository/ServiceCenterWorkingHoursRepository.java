package com.vehicare.modules.scheduling.repository;

import java.time.DayOfWeek;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vehicare.modules.scheduling.entity.ServiceCenterWorkingHours;

public interface ServiceCenterWorkingHoursRepository
        extends JpaRepository<ServiceCenterWorkingHours, Long> {

    Optional<ServiceCenterWorkingHours> findByDayOfWeek(
            DayOfWeek dayOfWeek);
}