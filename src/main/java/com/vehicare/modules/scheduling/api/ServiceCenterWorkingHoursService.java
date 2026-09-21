package com.vehicare.modules.scheduling.api;

import java.time.DayOfWeek;
import java.util.List;

import com.vehicare.modules.scheduling.dto.ServiceCenterWorkingHoursRequest;
import com.vehicare.modules.scheduling.dto.ServiceCenterWorkingHoursResponse;

public interface ServiceCenterWorkingHoursService {

    ServiceCenterWorkingHoursResponse create(
            ServiceCenterWorkingHoursRequest request);

    List<ServiceCenterWorkingHoursResponse> getAll();

    ServiceCenterWorkingHoursResponse getByDay(
            DayOfWeek dayOfWeek);

    ServiceCenterWorkingHoursResponse update(
            DayOfWeek dayOfWeek,
            ServiceCenterWorkingHoursRequest request);

    
}	