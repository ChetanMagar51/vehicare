package com.vehicare.modules.scheduling.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vehicare.modules.scheduling.api.SlotBookingService;
import com.vehicare.modules.scheduling.dto.BookSlotRequest;
import com.vehicare.modules.scheduling.dto.BookSlotResponse;
import com.vehicare.modules.scheduling.entity.ServiceSlot;
import com.vehicare.modules.scheduling.entity.ServiceSlotStatus;
import com.vehicare.modules.scheduling.exception.InvalidSlotException;
import com.vehicare.modules.scheduling.exception.ServiceSlotAlreadyBookedException;
import com.vehicare.modules.scheduling.exception.ServiceSlotNotFoundException;
import com.vehicare.modules.scheduling.repository.ServiceSlotRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SlotBookingServiceImpl implements SlotBookingService {

	private final ServiceSlotRepository serviceSlotRepository;

	@Override
	@Transactional
	public BookSlotResponse bookSlot(BookSlotRequest request) {

		ServiceSlot slot = serviceSlotRepository
		        .findWithLockById(request.getServiceSlotId())
		        .orElseThrow(() ->
		                new ServiceSlotNotFoundException(
		                        "Service slot not found"));
		// Verify slot belongs to selected advisor
		if (!slot.getServiceAdvisorId().equals(request.getServiceAdvisorId())) {

			throw new InvalidSlotException("Service slot does not belong to the selected advisor");
		}

		// Check slot status
		if (slot.getStatus() == ServiceSlotStatus.BLOCKED) {

			throw new InvalidSlotException("Service slot is blocked");
		}

		if (slot.getStatus() == ServiceSlotStatus.BOOKED) {

			throw new ServiceSlotAlreadyBookedException("Service slot is already booked");
		}

		// Book slot
		slot.setStatus(ServiceSlotStatus.BOOKED);

		ServiceSlot savedSlot = serviceSlotRepository.save(slot);

		return BookSlotResponse.builder().slotId(savedSlot.getId()).serviceAdvisorId(savedSlot.getServiceAdvisorId())
				.serviceDate(savedSlot.getServiceDate()).startTime(savedSlot.getStartTime())
				.endTime(savedSlot.getEndTime()).message("Service slot booked successfully").build();
	}

	@Override
	@Transactional
	public void releaseSlot(Long slotId) {

		 ServiceSlot slot = serviceSlotRepository
	                .findWithLockById(slotId)
	                .orElseThrow(() ->
	                        new ServiceSlotNotFoundException(
	                                "Service slot not found"));

		if (slot.getStatus() == ServiceSlotStatus.BOOKED) {
			slot.setStatus(ServiceSlotStatus.AVAILABLE);
			serviceSlotRepository.save(slot);
		}
	}
}