package com.vehicare.modules.serviceadvisor.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceAdvisorUpdateRequest {

	private String specialization;

	@Min(value = 0, message = "Experience cannot be negative")
	private Integer experience;

	private Boolean available;
}