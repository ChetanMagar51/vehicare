package com.vehicare.modules.scheduling.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vehicare.modules.scheduling.dto.BookSlotRequest;
import com.vehicare.modules.scheduling.dto.BookSlotResponse;
import com.vehicare.modules.scheduling.entity.ServiceSlot;
import com.vehicare.modules.scheduling.entity.ServiceSlotStatus;
import com.vehicare.modules.scheduling.exception.InvalidSlotException;
import com.vehicare.modules.scheduling.exception.ServiceSlotAlreadyBookedException;
import com.vehicare.modules.scheduling.exception.ServiceSlotNotFoundException;
import com.vehicare.modules.scheduling.repository.ServiceSlotRepository;

@ExtendWith(MockitoExtension.class)
class SlotBookingServiceImplTest {

    @Mock
    private ServiceSlotRepository serviceSlotRepository;

    @InjectMocks
    private SlotBookingServiceImpl slotBookingService;

    private static final Long SLOT_ID = 1L;
    private static final Long ADVISOR_ID = 101L;

    private static final LocalDate SERVICE_DATE =
            LocalDate.of(2026, 9, 21);

    private static final LocalTime START_TIME =
            LocalTime.of(9, 0);

    private static final LocalTime END_TIME =
            LocalTime.of(9, 30);

    private ServiceSlot availableSlot() {

        return ServiceSlot.builder()
                .id(SLOT_ID)
                .serviceAdvisorId(ADVISOR_ID)
                .serviceDate(SERVICE_DATE)
                .startTime(START_TIME)
                .endTime(END_TIME)
                .status(ServiceSlotStatus.AVAILABLE)
                .build();
    }

    private BookSlotRequest validRequest() {

        BookSlotRequest request = new BookSlotRequest();

        request.setServiceSlotId(SLOT_ID);
        request.setServiceAdvisorId(ADVISOR_ID);

        return request;
    }

    // ============================================================
    // bookSlot()
    // ============================================================

    @Test
    void bookSlot_shouldBookAvailableSlotSuccessfully() {

        ServiceSlot slot = availableSlot();

        BookSlotRequest request = validRequest();

        when(serviceSlotRepository.findWithLockById(SLOT_ID))
                .thenReturn(Optional.of(slot));

        when(serviceSlotRepository.save(slot))
                .thenReturn(slot);

        BookSlotResponse response =
                slotBookingService.bookSlot(request);

        assertEquals(
                SLOT_ID,
                response.getSlotId()
        );

        assertEquals(
                ADVISOR_ID,
                response.getServiceAdvisorId()
        );

        assertEquals(
                SERVICE_DATE,
                response.getServiceDate()
        );

        assertEquals(
                START_TIME,
                response.getStartTime()
        );

        assertEquals(
                END_TIME,
                response.getEndTime()
        );

        assertEquals(
                "Service slot booked successfully",
                response.getMessage()
        );

        assertEquals(
                ServiceSlotStatus.BOOKED,
                slot.getStatus()
        );

        verify(serviceSlotRepository)
                .findWithLockById(SLOT_ID);

        verify(serviceSlotRepository)
                .save(slot);
    }

    @Test
    void bookSlot_shouldThrowExceptionWhenSlotNotFound() {

        BookSlotRequest request = validRequest();

        when(serviceSlotRepository.findWithLockById(SLOT_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                ServiceSlotNotFoundException.class,
                () -> slotBookingService.bookSlot(request)
        );

        verify(serviceSlotRepository)
                .findWithLockById(SLOT_ID);

        verify(serviceSlotRepository, never())
                .save(any(ServiceSlot.class));
    }

    @Test
    void bookSlot_shouldThrowExceptionWhenSlotBelongsToDifferentAdvisor() {

        ServiceSlot slot = availableSlot();

        BookSlotRequest request = validRequest();

        request.setServiceAdvisorId(999L);

        when(serviceSlotRepository.findWithLockById(SLOT_ID))
                .thenReturn(Optional.of(slot));

        assertThrows(
                InvalidSlotException.class,
                () -> slotBookingService.bookSlot(request)
        );

        verify(serviceSlotRepository)
                .findWithLockById(SLOT_ID);

        verify(serviceSlotRepository, never())
                .save(any(ServiceSlot.class));
    }

    @Test
    void bookSlot_shouldThrowExceptionWhenSlotIsBlocked() {

        ServiceSlot slot = availableSlot();

        slot.setStatus(ServiceSlotStatus.BLOCKED);

        BookSlotRequest request = validRequest();

        when(serviceSlotRepository.findWithLockById(SLOT_ID))
                .thenReturn(Optional.of(slot));

        assertThrows(
                InvalidSlotException.class,
                () -> slotBookingService.bookSlot(request)
        );

        verify(serviceSlotRepository)
                .findWithLockById(SLOT_ID);

        verify(serviceSlotRepository, never())
                .save(any(ServiceSlot.class));
    }

    @Test
    void bookSlot_shouldThrowExceptionWhenSlotIsAlreadyBooked() {

        ServiceSlot slot = availableSlot();

        slot.setStatus(ServiceSlotStatus.BOOKED);

        BookSlotRequest request = validRequest();

        when(serviceSlotRepository.findWithLockById(SLOT_ID))
                .thenReturn(Optional.of(slot));

        assertThrows(
                ServiceSlotAlreadyBookedException.class,
                () -> slotBookingService.bookSlot(request)
        );

        verify(serviceSlotRepository)
                .findWithLockById(SLOT_ID);

        verify(serviceSlotRepository, never())
                .save(any(ServiceSlot.class));
    }

    // ============================================================
    // releaseSlot()
    // ============================================================

    @Test
    void releaseSlot_shouldChangeBookedSlotToAvailable() {

        ServiceSlot slot = availableSlot();

        slot.setStatus(ServiceSlotStatus.BOOKED);

        when(serviceSlotRepository.findWithLockById(SLOT_ID))
                .thenReturn(Optional.of(slot));

        when(serviceSlotRepository.save(slot))
                .thenReturn(slot);

        slotBookingService.releaseSlot(SLOT_ID);

        assertEquals(
                ServiceSlotStatus.AVAILABLE,
                slot.getStatus()
        );

        verify(serviceSlotRepository)
                .findWithLockById(SLOT_ID);

        verify(serviceSlotRepository)
                .save(slot);
    }

    @Test
    void releaseSlot_shouldNotSaveWhenSlotIsAvailable() {

        ServiceSlot slot = availableSlot();

        when(serviceSlotRepository.findWithLockById(SLOT_ID))
                .thenReturn(Optional.of(slot));

        slotBookingService.releaseSlot(SLOT_ID);

        assertEquals(
                ServiceSlotStatus.AVAILABLE,
                slot.getStatus()
        );

        verify(serviceSlotRepository)
                .findWithLockById(SLOT_ID);

        verify(serviceSlotRepository, never())
                .save(any(ServiceSlot.class));
    }

    @Test
    void releaseSlot_shouldNotSaveWhenSlotIsBlocked() {

        ServiceSlot slot = availableSlot();

        slot.setStatus(ServiceSlotStatus.BLOCKED);

        when(serviceSlotRepository.findWithLockById(SLOT_ID))
                .thenReturn(Optional.of(slot));

        slotBookingService.releaseSlot(SLOT_ID);

        assertEquals(
                ServiceSlotStatus.BLOCKED,
                slot.getStatus()
        );

        verify(serviceSlotRepository)
                .findWithLockById(SLOT_ID);

        verify(serviceSlotRepository, never())
                .save(any(ServiceSlot.class));
    }

    @Test
    void releaseSlot_shouldThrowExceptionWhenSlotNotFound() {

        when(serviceSlotRepository.findWithLockById(SLOT_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                ServiceSlotNotFoundException.class,
                () -> slotBookingService.releaseSlot(SLOT_ID)
        );

        verify(serviceSlotRepository)
                .findWithLockById(SLOT_ID);

        verify(serviceSlotRepository, never())
                .save(any(ServiceSlot.class));
    }
}