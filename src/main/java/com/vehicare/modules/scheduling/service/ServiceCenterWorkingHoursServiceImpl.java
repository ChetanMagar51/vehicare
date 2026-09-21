package com.vehicare.modules.scheduling.service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vehicare.modules.scheduling.api.ServiceCenterWorkingHoursService;
import com.vehicare.modules.scheduling.dto.ServiceCenterWorkingHoursRequest;
import com.vehicare.modules.scheduling.dto.ServiceCenterWorkingHoursResponse;
import com.vehicare.modules.scheduling.entity.ServiceCenterWorkingHours;
import com.vehicare.modules.scheduling.exception.InvalidSlotException;
import com.vehicare.modules.scheduling.repository.ServiceCenterWorkingHoursRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceCenterWorkingHoursServiceImpl implements ServiceCenterWorkingHoursService {

	private final ServiceCenterWorkingHoursRepository workingHoursRepository;

	@Override
	@Transactional
	public ServiceCenterWorkingHoursResponse create(ServiceCenterWorkingHoursRequest request) {

		validateRequest(request);

		if (workingHoursRepository.findByDayOfWeek(request.getDayOfWeek()).isPresent()) {

			throw new InvalidSlotException("Working hours already configured for " + request.getDayOfWeek());
		}

		ServiceCenterWorkingHours workingHours = ServiceCenterWorkingHours.builder().dayOfWeek(request.getDayOfWeek())
				.openingTime(request.getOpeningTime()).closingTime(request.getClosingTime())
				.breakStart(request.getBreakStart()).breakEnd(request.getBreakEnd()).closed(request.getClosed())
				.build();

		ServiceCenterWorkingHours saved = workingHoursRepository.save(workingHours);

		return mapToResponse(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ServiceCenterWorkingHoursResponse> getAll() {

		return workingHoursRepository.findAll().stream().map(this::mapToResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public ServiceCenterWorkingHoursResponse getByDay(DayOfWeek dayOfWeek) {

		if (dayOfWeek == null) {
			throw new InvalidSlotException("Day of week is required");
		}

		ServiceCenterWorkingHours workingHours = workingHoursRepository.findByDayOfWeek(dayOfWeek)
				.orElseThrow(() -> new InvalidSlotException("Working hours not found for " + dayOfWeek));

		return mapToResponse(workingHours);
	}

	@Override
	@Transactional
	public ServiceCenterWorkingHoursResponse update(DayOfWeek dayOfWeek, ServiceCenterWorkingHoursRequest request) {

		if (dayOfWeek == null) {
			throw new InvalidSlotException("Day of week is required");
		}

		validateRequest(request);

		ServiceCenterWorkingHours workingHours = workingHoursRepository.findByDayOfWeek(dayOfWeek)
				.orElseThrow(() -> new InvalidSlotException("Working hours not found for " + dayOfWeek));

		if (!dayOfWeek.equals(request.getDayOfWeek())) {
			throw new InvalidSlotException("Day of week cannot be changed during update");
		}

		workingHours.setOpeningTime(request.getOpeningTime());
		workingHours.setClosingTime(request.getClosingTime());
		workingHours.setBreakStart(request.getBreakStart());
		workingHours.setBreakEnd(request.getBreakEnd());
		workingHours.setClosed(request.getClosed());

		ServiceCenterWorkingHours updated = workingHoursRepository.save(workingHours);

		return mapToResponse(updated);
	}

	

	private void validateRequest(ServiceCenterWorkingHoursRequest request) {

		if (request == null) {
			throw new InvalidSlotException("Working hours request is required");
		}

		if (request.getDayOfWeek() == null) {
			throw new InvalidSlotException("Day of week is required");
		}

		if (request.getOpeningTime() == null || request.getClosingTime() == null) {

			throw new InvalidSlotException("Opening time and closing time are required");
		}

		if (!request.getOpeningTime().isBefore(request.getClosingTime())) {

			throw new InvalidSlotException("Opening time must be before closing time");
		}

		validateBreak(request);
	}

	private void validateBreak(ServiceCenterWorkingHoursRequest request) {

		LocalTime breakStart = request.getBreakStart();
		LocalTime breakEnd = request.getBreakEnd();

		if ((breakStart == null) != (breakEnd == null)) {

			throw new InvalidSlotException("Both break start and break end are required");
		}

		if (breakStart == null) {
			return;
		}

		if (!breakStart.isBefore(breakEnd)) {

			throw new InvalidSlotException("Break start time must be before break end time");
		}

		if (breakStart.isBefore(request.getOpeningTime()) || breakEnd.isAfter(request.getClosingTime())) {

			throw new InvalidSlotException("Break must be within center working hours");
		}
	}

	private ServiceCenterWorkingHoursResponse mapToResponse(ServiceCenterWorkingHours workingHours) {

		return ServiceCenterWorkingHoursResponse.builder().id(workingHours.getId())
				.dayOfWeek(workingHours.getDayOfWeek()).openingTime(workingHours.getOpeningTime())
				.closingTime(workingHours.getClosingTime()).breakStart(workingHours.getBreakStart())
				.breakEnd(workingHours.getBreakEnd()).closed(workingHours.getClosed()).build();
	}
}