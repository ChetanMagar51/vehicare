package com.vehicare.modules.inventory.api;

import java.util.List;

import com.vehicare.modules.inventory.dto.PartDto;
import com.vehicare.modules.inventory.dto.PartRequest;

public interface InventoryService {

	PartDto createPart(PartRequest request);

	PartDto getPartById(Long id);

	List<PartDto> getAllParts();

	PartDto updatePart(Long id, PartRequest request);

	void activatePart(Long id);

	void deactivatePart(Long id);
	

	// for admin
	void addStock(Long partId, Integer quantity);

	void removeStock(Long partId, Integer quantity, String reason);

	boolean isStockAvailable(Long partId, Integer quantity);

    //servicing
	void useStock(Long partId, Integer quantity, Long serviceRecordId);

	void returnStock(Long partId, Integer quantity, Long serviceRecordId);
}