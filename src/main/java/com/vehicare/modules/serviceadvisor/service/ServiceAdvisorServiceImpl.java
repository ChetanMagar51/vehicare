package com.vehicare.modules.serviceadvisor.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.vehicare.modules.serviceadvisor.api.ServiceAdvisorService;
import com.vehicare.modules.serviceadvisor.dto.ServiceAdvisorCreateRequest;
import com.vehicare.modules.serviceadvisor.dto.ServiceAdvisorResponse;
import com.vehicare.modules.serviceadvisor.dto.ServiceAdvisorUpdateRequest;
import com.vehicare.modules.serviceadvisor.entity.ServiceAdvisorProfile;
import com.vehicare.modules.serviceadvisor.exception.ServiceAdvisorProfileAlreadyExistsException;
import com.vehicare.modules.serviceadvisor.exception.ServiceAdvisorProfileNotFoundException;
import com.vehicare.modules.serviceadvisor.repository.ServiceAdvisorProfileRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class ServiceAdvisorServiceImpl implements ServiceAdvisorService {

	private final ServiceAdvisorProfileRepository profileRepository;

	@Override
	public ServiceAdvisorResponse createProfile(ServiceAdvisorCreateRequest request) {

		if (profileRepository.existsByUserId(request.getUserId())) {
			throw new ServiceAdvisorProfileAlreadyExistsException("Profile already exists for this user");
		}

		if (profileRepository.existsByEmployeeId(request.getEmployeeId())) {
			throw new ServiceAdvisorProfileAlreadyExistsException("Employee ID already exists");
		}

		ServiceAdvisorProfile profile = ServiceAdvisorProfile.builder().userId(request.getUserId())
				.employeeId(request.getEmployeeId()).specialization(request.getSpecialization())
				.experience(request.getExperience())
				.available(request.getAvailable() != null ? request.getAvailable() : true).build();

		ServiceAdvisorProfile savedProfile = profileRepository.save(profile);

		return mapToResponse(savedProfile);
	}

	@Override
	@Transactional(readOnly = true)
	public ServiceAdvisorResponse getProfileByUserId(Long userId) {

		ServiceAdvisorProfile profile = profileRepository.findByUserId(userId)
				.orElseThrow(() -> new ServiceAdvisorProfileNotFoundException("Service advisor profile not found"));

		return mapToResponse(profile);
	}

	@Override
	public ServiceAdvisorResponse updateProfile(Long userId, ServiceAdvisorUpdateRequest request) {

		ServiceAdvisorProfile profile = profileRepository.findByUserId(userId)
				.orElseThrow(() -> new ServiceAdvisorProfileNotFoundException("Service advisor profile not found"));

		if (request.getSpecialization() != null) {
			profile.setSpecialization(request.getSpecialization());
		}

		if (request.getExperience() != null) {
			profile.setExperience(request.getExperience());
		}

		if (request.getAvailable() != null) {
			profile.setAvailable(request.getAvailable());
		}

		ServiceAdvisorProfile updatedProfile = profileRepository.save(profile);

		return mapToResponse(updatedProfile);
	}

	private ServiceAdvisorResponse mapToResponse(ServiceAdvisorProfile profile) {

		return ServiceAdvisorResponse.builder().id(profile.getId()).userId(profile.getUserId())
				.employeeId(profile.getEmployeeId()).specialization(profile.getSpecialization())
				.experience(profile.getExperience()).available(profile.getAvailable()).build();
	}
}