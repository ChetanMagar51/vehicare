package com.vehicare.modules.serviceadvisor.dto;

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
public class ServiceAdvisorResponse {

	private Long id;

	private Long userId;

	private String employeeId;

	private String specialization;

	private Integer experience;

	private Boolean available;
}