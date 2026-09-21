package com.vehicare.modules.scheduling.service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vehicare.modules.scheduling.api.ServiceAdvisorAvailabilityService;
import com.vehicare.modules.scheduling.dto.ServiceAdvisorAvailabilityRequest;
import com.vehicare.modules.scheduling.dto.ServiceAdvisorAvailabilityResponse;
import com.vehicare.modules.scheduling.entity.ServiceAdvisorAvailability;
import com.vehicare.modules.scheduling.exception.InvalidSlotException;
import com.vehicare.modules.scheduling.exception.ServiceAdvisorNotFoundException;
import com.vehicare.modules.scheduling.repository.ServiceAdvisorAvailabilityRepository;
import com.vehicare.modules.user.dto.UserDto;
import com.vehicare.modules.user.entity.Role;
import com.vehicare.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceAdvisorAvailabilityServiceImpl implements ServiceAdvisorAvailabilityService {

	private final ServiceAdvisorAvailabilityRepository availabilityRepository;
	private final UserService userService;

	@Override
	@Transactional
	public ServiceAdvisorAvailabilityResponse create(ServiceAdvisorAvailabilityRequest request) {

		validateRequest(request);

		validateServiceAdvisor(request.getServiceAdvisorId());

		if (Boolean.TRUE.equals(request.getAvailable())) {
		    validateNoOverlap(
		            request.getServiceAdvisorId(),
		            request.getDayOfWeek(),
		            request.getAvailableFrom(),
		            request.getAvailableTo(),
		            null);
		}
		ServiceAdvisorAvailability availability = ServiceAdvisorAvailability.builder()
				.serviceAdvisorId(request.getServiceAdvisorId()).dayOfWeek(request.getDayOfWeek())
				.availableFrom(request.getAvailableFrom()).availableTo(request.getAvailableTo())
				.available(request.getAvailable()).build();

		ServiceAdvisorAvailability saved = availabilityRepository.save(availability);

		return mapToResponse(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public ServiceAdvisorAvailabilityResponse getById(Long id) {

		if (id == null) {
			throw new InvalidSlotException("Availability ID is required");
		}

		ServiceAdvisorAvailability availability = availabilityRepository.findById(id)
				.orElseThrow(() -> new InvalidSlotException("Advisor availability not found"));

		return mapToResponse(availability);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ServiceAdvisorAvailabilityResponse> getByAdvisorId(Long serviceAdvisorId) {

		validateServiceAdvisor(serviceAdvisorId);

		return availabilityRepository.findByServiceAdvisorId(serviceAdvisorId).stream().map(this::mapToResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ServiceAdvisorAvailabilityResponse> getByAdvisorIdAndDay(Long serviceAdvisorId, DayOfWeek dayOfWeek) {

		validateServiceAdvisor(serviceAdvisorId);

		if (dayOfWeek == null) {
			throw new InvalidSlotException("Day of week is required");
		}

		return availabilityRepository.findByServiceAdvisorIdAndDayOfWeek(serviceAdvisorId, dayOfWeek).stream()
				.map(this::mapToResponse).toList();
	}

	@Override
	@Transactional
	public ServiceAdvisorAvailabilityResponse update(Long id, ServiceAdvisorAvailabilityRequest request) {

		if (id == null) {
			throw new InvalidSlotException("Availability ID is required");
		}

		validateRequest(request);

		validateServiceAdvisor(request.getServiceAdvisorId());

		ServiceAdvisorAvailability availability = availabilityRepository.findById(id)
				.orElseThrow(() -> new InvalidSlotException("Advisor availability not found"));

		if (Boolean.TRUE.equals(request.getAvailable())) {
		    validateNoOverlap(
		            request.getServiceAdvisorId(),
		            request.getDayOfWeek(),
		            request.getAvailableFrom(),
		            request.getAvailableTo(),
		            id);
		}
		availability.setServiceAdvisorId(request.getServiceAdvisorId());
		availability.setDayOfWeek(request.getDayOfWeek());
		availability.setAvailableFrom(request.getAvailableFrom());
		availability.setAvailableTo(request.getAvailableTo());
		availability.setAvailable(request.getAvailable());

		ServiceAdvisorAvailability updated = availabilityRepository.save(availability);

		return mapToResponse(updated);
	}

	private void validateRequest(ServiceAdvisorAvailabilityRequest request) {

		if (request == null) {
			throw new InvalidSlotException("Availability request is required");
		}

		if (request.getServiceAdvisorId() == null) {
			throw new InvalidSlotException("Service advisor ID is required");
		}

		if (request.getDayOfWeek() == null) {
			throw new InvalidSlotException("Day of week is required");
		}

		if (request.getAvailableFrom() == null || request.getAvailableTo() == null) {

			throw new InvalidSlotException("Available from and available to are required");
		}

		if (!request.getAvailableFrom().isBefore(request.getAvailableTo())) {

			throw new InvalidSlotException("Available from must be before available to");
		}

		if (request.getAvailable() == null) {
			throw new InvalidSlotException("Availability status is required");
		}
	}

	private void validateServiceAdvisor(Long serviceAdvisorId) {

		UserDto advisor;

		try {
			advisor = userService.getUserById(serviceAdvisorId);
		} catch (Exception ex) {
			throw new ServiceAdvisorNotFoundException("Service advisor not found");
		}

		if (advisor.getRole() != Role.Service_Adviser) {
			throw new ServiceAdvisorNotFoundException("User is not a service advisor");
		}
	}

	private void validateNoOverlap(Long serviceAdvisorId, DayOfWeek dayOfWeek, LocalTime availableFrom,
			LocalTime availableTo, Long currentId) {

		List<ServiceAdvisorAvailability> existing = availabilityRepository
				.findByServiceAdvisorIdAndDayOfWeek(serviceAdvisorId, dayOfWeek);

		boolean overlap = existing.stream().filter(a -> !a.getId().equals(currentId))
				.filter(a -> Boolean.TRUE.equals(a.getAvailable()))
				.anyMatch(a -> availableFrom.isBefore(a.getAvailableTo()) && availableTo.isAfter(a.getAvailableFrom()));

		if (overlap) {
			throw new InvalidSlotException("Advisor availability overlaps with an existing availability");
		}
	}

	private ServiceAdvisorAvailabilityResponse mapToResponse(ServiceAdvisorAvailability availability) {

		return ServiceAdvisorAvailabilityResponse.builder().id(availability.getId())
				.serviceAdvisorId(availability.getServiceAdvisorId()).dayOfWeek(availability.getDayOfWeek())
				.availableFrom(availability.getAvailableFrom()).availableTo(availability.getAvailableTo())
				.available(availability.getAvailable()).build();
	}
}