package com.vehicare.modules.scheduling.repository;

import java.time.DayOfWeek;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vehicare.modules.scheduling.entity.ServiceAdvisorAvailability;

public interface ServiceAdvisorAvailabilityRepository
        extends JpaRepository<ServiceAdvisorAvailability, Long> {

    List<ServiceAdvisorAvailability> findByDayOfWeek(
            DayOfWeek dayOfWeek);

    List<ServiceAdvisorAvailability> findByServiceAdvisorId(
            Long serviceAdvisorId);

    List<ServiceAdvisorAvailability> findByServiceAdvisorIdAndDayOfWeek(
            Long serviceAdvisorId,
            DayOfWeek dayOfWeek);
}