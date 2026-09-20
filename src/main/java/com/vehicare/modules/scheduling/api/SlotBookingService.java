package com.vehicare.modules.scheduling.api;

import com.vehicare.modules.scheduling.dto.BookSlotRequest;
import com.vehicare.modules.scheduling.dto.BookSlotResponse;

public interface SlotBookingService {

    BookSlotResponse bookSlot(BookSlotRequest request);

    void releaseSlot(Long slotId);
}