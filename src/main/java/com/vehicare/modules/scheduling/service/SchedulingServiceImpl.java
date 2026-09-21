package com.vehicare.modules.scheduling.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vehicare.modules.scheduling.api.SchedulingService;
import com.vehicare.modules.scheduling.dto.AvailableSlotResponse;
import com.vehicare.modules.scheduling.entity.ServiceAdvisorAvailability;
import com.vehicare.modules.scheduling.entity.ServiceCenterWorkingHours;
import com.vehicare.modules.scheduling.entity.ServiceSlot;
import com.vehicare.modules.scheduling.entity.ServiceSlotStatus;
import com.vehicare.modules.scheduling.exception.InvalidSchedulingConfigurationException;
import com.vehicare.modules.scheduling.exception.InvalidSlotException;
import com.vehicare.modules.scheduling.exception.SchedulingException;
import com.vehicare.modules.scheduling.exception.ServiceAdvisorAvailabilityNotFoundException;
import com.vehicare.modules.scheduling.exception.ServiceAdvisorNotFoundException;
import com.vehicare.modules.scheduling.exception.WorkingHoursNotConfiguredException;
import com.vehicare.modules.scheduling.repository.ServiceAdvisorAvailabilityRepository;
import com.vehicare.modules.scheduling.repository.ServiceCenterWorkingHoursRepository;
import com.vehicare.modules.scheduling.repository.ServiceSlotRepository;
import com.vehicare.modules.user.dto.UserDto;
import com.vehicare.modules.user.entity.Role;
import com.vehicare.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SchedulingServiceImpl implements SchedulingService {

	private final ServiceSlotRepository serviceSlotRepository;
	private final ServiceCenterWorkingHoursRepository workingHoursRepository;
	private final ServiceAdvisorAvailabilityRepository advisorAvailabilityRepository;
	private final UserService userService;

	@Value("${scheduling.slot-duration-minutes}")
	private Integer slotDurationMinutes;

	@Override
	@Transactional
	public void generateSlotsForAdvisor(Long serviceAdvisorId, LocalDate startDate, LocalDate endDate) {

		// 1. Basic validation
		validateAdvisorId(serviceAdvisorId);
		validateDates(startDate, endDate);
		validateSlotDuration();

		// 2. Validate that user is actually a Service Advisor
		validateServiceAdvisor(serviceAdvisorId);

		

		// 3. Load center working hours ONCE
		Map<DayOfWeek, ServiceCenterWorkingHours> workingHoursMap = loadWorkingHours();

		// 4. Load advisor availability ONCE
		Map<DayOfWeek, List<ServiceAdvisorAvailability>> availabilityMap = loadAdvisorAvailability(serviceAdvisorId);

		// 5. Validate advisor availability configuration ONCE
		validateAdvisorAvailability(availabilityMap);

		// 6. Load all existing slots for this advisor and month ONCE
		List<ServiceSlot> existingSlots = serviceSlotRepository
				.findByServiceAdvisorIdAndServiceDateBetween(serviceAdvisorId, startDate, endDate);

		// 7. Put existing slots into memory
		Set<SlotKey> existingSlotKeys = existingSlots.stream()
				.map(slot -> new SlotKey(slot.getServiceDate(), slot.getStartTime(), slot.getEndTime()))
				.collect(Collectors.toSet());

		// 8. Store newly generated slots in memory
		List<ServiceSlot> newSlots = new ArrayList<>();

		// 9. Generate slots for every day of the month
		LocalDate currentDate = startDate;

		while (!currentDate.isAfter(endDate)) {

			DayOfWeek dayOfWeek = currentDate.getDayOfWeek();

			ServiceCenterWorkingHours centerHours = workingHoursMap.get(dayOfWeek);

			// Center must have configuration for every day
			if (centerHours == null) {
				throw new WorkingHoursNotConfiguredException("Working hours not configured for " + dayOfWeek);
			}

			// If center is closed, don't generate slots
			if (Boolean.TRUE.equals(centerHours.getClosed())) {
				currentDate = currentDate.plusDays(1);
				continue;
			}

			// Validate center working hours
			validateWorkingHours(centerHours);

			// Get this advisor's availability for this day
			List<ServiceAdvisorAvailability> availabilities = availabilityMap.getOrDefault(dayOfWeek,
					new ArrayList<>());

			// No availability = no slots
			for (ServiceAdvisorAvailability availability : availabilities) {

				if (!Boolean.TRUE.equals(availability.getAvailable())) {
					continue;
				}

				

				// Find the common time between:
				// Service Center working hours
				// AND
				// Advisor availability

				LocalTime effectiveStart = getLaterTime(centerHours.getOpeningTime(), availability.getAvailableFrom());

				LocalTime effectiveEnd = getEarlierTime(centerHours.getClosingTime(), availability.getAvailableTo());

				// No common working time
				if (!effectiveStart.isBefore(effectiveEnd)) {
					continue;
				}

				// Generate slots only in memory
				generateSlotsInMemory(serviceAdvisorId, currentDate, effectiveStart, effectiveEnd, centerHours,
						existingSlotKeys, newSlots);
			}

			currentDate = currentDate.plusDays(1);
		}

		// 10. Save all new slots with one DB operation
		if (!newSlots.isEmpty()) {
			serviceSlotRepository.saveAll(newSlots);
		}
	}

	// --------------------------------------------------
	// Generate slots in memory
	// --------------------------------------------------

	private void generateSlotsInMemory(Long serviceAdvisorId, LocalDate date, LocalTime startTime, LocalTime endTime,
			ServiceCenterWorkingHours centerHours, Set<SlotKey> existingSlotKeys, List<ServiceSlot> newSlots) {

		LocalTime currentTime = startTime;

		while (true) {

			LocalTime slotEnd = currentTime.plusMinutes(slotDurationMinutes);

			// Slot must completely fit inside advisor/center time
			if (slotEnd.isAfter(endTime)) {
				break;
			}

			// Skip slots that overlap center break
			if (!overlapsBreak(currentTime, slotEnd, centerHours)) {

				SlotKey key = new SlotKey(date, currentTime, slotEnd);

				// add() returns false if slot already exists
				if (existingSlotKeys.add(key)) {

					ServiceSlot slot = ServiceSlot.builder().serviceAdvisorId(serviceAdvisorId).serviceDate(date)
							.startTime(currentTime).endTime(slotEnd).status(ServiceSlotStatus.AVAILABLE).build();

					newSlots.add(slot);
				}
			}

			currentTime = slotEnd;
		}
	}

	// --------------------------------------------------
	// Load working hours once
	// --------------------------------------------------

	private Map<DayOfWeek, ServiceCenterWorkingHours> loadWorkingHours() {

		List<ServiceCenterWorkingHours> workingHours = workingHoursRepository.findAll();

		Map<DayOfWeek, ServiceCenterWorkingHours> map = workingHours.stream()
				.collect(Collectors.toMap(ServiceCenterWorkingHours::getDayOfWeek, Function.identity()));

		// Validate all days are configured
		for (DayOfWeek day : DayOfWeek.values()) {

			if (!map.containsKey(day)) {
				throw new WorkingHoursNotConfiguredException("Working hours not configured for " + day);
			}
		}

		return map;
	}

	// --------------------------------------------------
	// Load advisor availability once
	// --------------------------------------------------

	private Map<DayOfWeek, List<ServiceAdvisorAvailability>> loadAdvisorAvailability(Long serviceAdvisorId) {

		List<ServiceAdvisorAvailability> availabilities = advisorAvailabilityRepository
				.findByServiceAdvisorId(serviceAdvisorId);
		
		if (availabilities.isEmpty()) {
	        throw new ServiceAdvisorAvailabilityNotFoundException(
	                "Service advisor availability is not configured");
	    }
		
		 for (ServiceAdvisorAvailability availability : availabilities) {
		        if (availability.getDayOfWeek() == null) {
		            throw new InvalidSlotException("Day of week is required");
		        }
		    }

		return availabilities.stream().collect(Collectors.groupingBy(ServiceAdvisorAvailability::getDayOfWeek));
	}

	// --------------------------------------------------
	// Advisor validation
	// --------------------------------------------------

	private void validateServiceAdvisor(Long serviceAdvisorId) {

		UserDto advisor = userService.getUserById(serviceAdvisorId);

		if (!(advisor.getRole() == Role.Service_Adviser)) {
			throw new ServiceAdvisorNotFoundException("Service advisor not found");
		}
	}

	// --------------------------------------------------
	// Basic validations
	// --------------------------------------------------

	private void validateAdvisorId(Long serviceAdvisorId) {

		if (serviceAdvisorId == null) {
			throw new InvalidSchedulingConfigurationException("Service advisor ID is required");
		}
	}

	private void validateDates(LocalDate startDate, LocalDate endDate) {
		
		
		if (startDate == null || endDate == null) {
			throw new InvalidSchedulingConfigurationException("dates is required");
		}
		
		if (startDate.isAfter(endDate)) {
	        throw new InvalidSchedulingConfigurationException(
	                "Start date must be before or equal to end date");
	    }
	}

	private void validateSlotDuration() {

		if (slotDurationMinutes == null || slotDurationMinutes <= 0) {

			throw new InvalidSchedulingConfigurationException("Slot duration must be greater than zero");
		}
	}

	// --------------------------------------------------
	// Working hours validation
	// --------------------------------------------------

	private void validateWorkingHours(ServiceCenterWorkingHours workingHours) {

		if (workingHours.getOpeningTime() == null || workingHours.getClosingTime() == null) {

			throw new InvalidSlotException("Opening and closing time are required for " + workingHours.getDayOfWeek());
		}

		if (!workingHours.getOpeningTime().isBefore(workingHours.getClosingTime())) {

			throw new InvalidSlotException(
					"Opening time must be before closing time for " + workingHours.getDayOfWeek());
		}

		LocalTime breakStart = workingHours.getBreakStart();

		LocalTime breakEnd = workingHours.getBreakEnd();

		// Both must be present or both must be absent
		if ((breakStart == null) != (breakEnd == null)) {

			throw new InvalidSlotException("Both break start and break end are required");
		}

		if (breakStart != null && breakEnd != null) {

			if (!breakStart.isBefore(breakEnd)) {

				throw new InvalidSlotException("Break start time must be before break end time");
			}

			if (breakStart.isBefore(workingHours.getOpeningTime()) || breakEnd.isAfter(workingHours.getClosingTime())) {

				throw new InvalidSlotException("Break must be within center working hours");
			}
		}
	}

	// --------------------------------------------------
	// Advisor availability validation
	// --------------------------------------------------

	private void validateAdvisorAvailability(Map<DayOfWeek, List<ServiceAdvisorAvailability>> availabilityMap) {

		for (Map.Entry<DayOfWeek, List<ServiceAdvisorAvailability>> entry : availabilityMap.entrySet()) {

			List<ServiceAdvisorAvailability> availabilities = entry.getValue();

			for (ServiceAdvisorAvailability availability : availabilities) {

				if (availability.getDayOfWeek() == null) {

					throw new InvalidSlotException("Day of week is required");
				}

				if (availability.getAvailableFrom() == null || availability.getAvailableTo() == null) {

					throw new InvalidSlotException("Advisor availability time is required");
				}

				if (!availability.getAvailableFrom().isBefore(availability.getAvailableTo())) {

					throw new InvalidSlotException("Advisor availableFrom must be before " + "availableTo");
				}
			}

			// Check overlapping availability periods
			validateNoOverlappingAvailability(availabilities);
		}
	}


	// --------------------------------------------------
	// Check overlapping advisor availability
	// --------------------------------------------------

	private void validateNoOverlappingAvailability(List<ServiceAdvisorAvailability> availabilities) {

		List<ServiceAdvisorAvailability> activeAvailabilities = availabilities.stream()
				.filter(a -> Boolean.TRUE.equals(a.getAvailable()))
				.sorted(Comparator.comparing(ServiceAdvisorAvailability::getAvailableFrom)).toList();

		for (int i = 0; i < activeAvailabilities.size() - 1; i++) {

			ServiceAdvisorAvailability current = activeAvailabilities.get(i);

			ServiceAdvisorAvailability next = activeAvailabilities.get(i + 1);

			if (next.getAvailableFrom().isBefore(current.getAvailableTo())) {

				throw new InvalidSlotException("Overlapping advisor availability found for " + current.getDayOfWeek());
			}
		}
	}

	// --------------------------------------------------
	// Break overlap
	// --------------------------------------------------

	private boolean overlapsBreak(LocalTime slotStart, LocalTime slotEnd, ServiceCenterWorkingHours centerHours) {

		LocalTime breakStart = centerHours.getBreakStart();

		LocalTime breakEnd = centerHours.getBreakEnd();

		if (breakStart == null || breakEnd == null) {
			return false;
		}

		return slotStart.isBefore(breakEnd) && slotEnd.isAfter(breakStart);
	}

	// --------------------------------------------------
	// Time helpers
	// --------------------------------------------------

	private LocalTime getLaterTime(LocalTime first, LocalTime second) {

		return first.isAfter(second) ? first : second;
	}

	private LocalTime getEarlierTime(LocalTime first, LocalTime second) {

		return first.isBefore(second) ? first : second;
	}

	// --------------------------------------------------
	// Slot key
	// --------------------------------------------------

	private record SlotKey(LocalDate date, LocalTime startTime, LocalTime endTime) {
	}

	// --------------------------------------------------
	// Existing methods
	// --------------------------------------------------

	@Override
	public List<AvailableSlotResponse> getAvailableSlots(Long serviceAdvisorId, LocalDate date) {

		return serviceSlotRepository
				.findByServiceAdvisorIdAndServiceDateAndStatus(serviceAdvisorId, date, ServiceSlotStatus.AVAILABLE)
				.stream().map(slot -> AvailableSlotResponse.builder().slotId(slot.getId())
						.startTime(slot.getStartTime()).endTime(slot.getEndTime()).build())
				.toList();
	}


	@Override
    @Transactional
    public void generateMonthlySlots(YearMonth month) {

		
		
		if (month == null) {
	        throw new InvalidSchedulingConfigurationException(
	                "Month is required");
	    }
		
		
        List<Long> advisorIds =
                userService .getUsersByRole(Role.Service_Adviser).stream().map(user->user.getId()).toList();

        for (Long advisorId : advisorIds) {
        	
        	LocalDate startDate = month.atDay(1);
    		LocalDate endDate = month.atEndOfMonth();

    		 try {
    	            generateSlotsForAdvisor(
    	                    advisorId,
    	                    startDate,
    	                    endDate
    	            );

    	        } catch (SchedulingException ex) {

    	            // One advisor's configuration problem
    	            // should not stop other advisors.
    	            // Log the error here.
    	        	
    	        	System.err.println("Failed to generate slots for service advisor {"+advisorId+"} "
    	        			+ "for month {"+month+"}"+ex);
      	        	     
    	        	
    	        	
    	        }


        }
    }

}