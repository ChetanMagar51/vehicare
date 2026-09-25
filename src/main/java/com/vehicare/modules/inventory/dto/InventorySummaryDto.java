package com.vehicare.modules.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventorySummaryDto {

    private long totalParts;

    private long totalQuantity;

    private long lowStockParts;

    private long outOfStockParts;

    private long slowMovingParts;

    private long highDemandParts;

    private double totalStockValue;
}