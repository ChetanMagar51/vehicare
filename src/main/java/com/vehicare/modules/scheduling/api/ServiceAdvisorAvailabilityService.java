package com.vehicare.modules.scheduling.api;

import java.time.DayOfWeek;
import java.util.List;

import com.vehicare.modules.scheduling.dto.ServiceAdvisorAvailabilityRequest;
import com.vehicare.modules.scheduling.dto.ServiceAdvisorAvailabilityResponse;

public interface ServiceAdvisorAvailabilityService {

	ServiceAdvisorAvailabilityResponse create(ServiceAdvisorAvailabilityRequest request);

	ServiceAdvisorAvailabilityResponse getById(Long id);

	List<ServiceAdvisorAvailabilityResponse> getByAdvisorId(Long serviceAdvisorId);

	List<ServiceAdvisorAvailabilityResponse> getByAdvisorIdAndDay(Long serviceAdvisorId, DayOfWeek dayOfWeek);

	ServiceAdvisorAvailabilityResponse update(Long id, ServiceAdvisorAvailabilityRequest request);

	
}