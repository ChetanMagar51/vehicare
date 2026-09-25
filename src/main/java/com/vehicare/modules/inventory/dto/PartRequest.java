package com.vehicare.modules.inventory.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartRequest {

	@NotBlank(message = "Part number is required")
	private String partNumber;

	@NotBlank(message = "Part name is required")
	private String name;

	private String description;

	@NotNull(message = "Quantity is required")
	@PositiveOrZero(message = "Quantity cannot be negative")
	private Integer quantity;

	@NotNull(message = "Unit price is required")
	@DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than zero")
	private Double unitPrice;
}