package com.vehicare.modules.inventory.api;

import java.util.List;

import com.vehicare.modules.inventory.dto.InventorySummaryDto;
import com.vehicare.modules.inventory.dto.PartDto;

public interface InventoryReportService {

	List<PartDto> getLowStockParts();

	List<PartDto> getOutOfStockParts();
	
	List<PartDto> getSlowMovingParts();
	
	List<PartDto> getHighDemandParts();
	
	InventorySummaryDto getInventorySummary();

}
