package com.vehicare.modules.serviceadvisor.api;

import java.util.List;

import com.vehicare.modules.serviceadvisor.dto.ServiceAdvisorScheduleResponse;

public interface ServiceAdvisorScheduleService {

	List<ServiceAdvisorScheduleResponse> getMyScheduledServices(Long serviceAdvisorId);

	ServiceAdvisorScheduleResponse getScheduledServiceById(Long serviceAdvisorId, Long scheduleId);
}