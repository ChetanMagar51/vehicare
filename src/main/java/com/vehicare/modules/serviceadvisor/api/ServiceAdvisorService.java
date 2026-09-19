package com.vehicare.modules.serviceadvisor.api;

import com.vehicare.modules.serviceadvisor.dto.ServiceAdvisorCreateRequest;
import com.vehicare.modules.serviceadvisor.dto.ServiceAdvisorResponse;
import com.vehicare.modules.serviceadvisor.dto.ServiceAdvisorUpdateRequest;

public interface ServiceAdvisorService {

	ServiceAdvisorResponse createProfile(ServiceAdvisorCreateRequest request);

	ServiceAdvisorResponse getProfileByUserId(Long userId);

	ServiceAdvisorResponse updateProfile(Long userId, ServiceAdvisorUpdateRequest request);
}