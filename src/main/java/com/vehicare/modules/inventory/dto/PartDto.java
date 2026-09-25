package com.vehicare.modules.inventory.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartDto {

	private Long id;
	private String partNumber;
	private String name;
	private String description;
	private Integer quantity;
	private Double unitPrice;
	private boolean active;
	private Integer minimumStockLevel;
}