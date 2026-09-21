package com.vehicare.modules.scheduling.controller;

import java.time.DayOfWeek;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vehicare.common.api.ApiResponse;
import com.vehicare.modules.scheduling.api.ServiceAdvisorAvailabilityService;
import com.vehicare.modules.scheduling.dto.ServiceAdvisorAvailabilityRequest;
import com.vehicare.modules.scheduling.dto.ServiceAdvisorAvailabilityResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/scheduling/advisor-availability")
@RequiredArgsConstructor
public class ServiceAdvisorAvailabilityController {

	private final ServiceAdvisorAvailabilityService availabilityService;

	@PostMapping
	public ResponseEntity<ApiResponse<ServiceAdvisorAvailabilityResponse>> create(
			@Valid @RequestBody ServiceAdvisorAvailabilityRequest request) {

		ServiceAdvisorAvailabilityResponse data = availabilityService.create(request);

		ApiResponse<ServiceAdvisorAvailabilityResponse> response = ApiResponse
				.<ServiceAdvisorAvailabilityResponse>builder().status("SUCCESS")
				.message("Advisor availability created successfully").data(data).build();

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<ServiceAdvisorAvailabilityResponse>> getById(@PathVariable Long id) {

		ServiceAdvisorAvailabilityResponse data = availabilityService.getById(id);

		ApiResponse<ServiceAdvisorAvailabilityResponse> response = ApiResponse
				.<ServiceAdvisorAvailabilityResponse>builder().status("SUCCESS")
				.message("Advisor availability retrieved successfully").data(data).build();

		return ResponseEntity.ok(response);
	}

	@GetMapping("/advisor/{serviceAdvisorId}")
	public ResponseEntity<ApiResponse<List<ServiceAdvisorAvailabilityResponse>>> getByAdvisorId(
			@PathVariable Long serviceAdvisorId) {

		List<ServiceAdvisorAvailabilityResponse> data = availabilityService.getByAdvisorId(serviceAdvisorId);

		ApiResponse<List<ServiceAdvisorAvailabilityResponse>> response = ApiResponse
				.<List<ServiceAdvisorAvailabilityResponse>>builder().status("SUCCESS")
				.message("Advisor availability retrieved successfully").data(data).build();

		return ResponseEntity.ok(response);
	}

	@GetMapping("/advisor/{serviceAdvisorId}/{dayOfWeek}")
	public ResponseEntity<ApiResponse<List<ServiceAdvisorAvailabilityResponse>>> getByAdvisorIdAndDay(
			@PathVariable Long serviceAdvisorId, @PathVariable DayOfWeek dayOfWeek) {

		List<ServiceAdvisorAvailabilityResponse> data = availabilityService.getByAdvisorIdAndDay(serviceAdvisorId,
				dayOfWeek);

		ApiResponse<List<ServiceAdvisorAvailabilityResponse>> response = ApiResponse
				.<List<ServiceAdvisorAvailabilityResponse>>builder().status("SUCCESS")
				.message("Advisor availability retrieved successfully").data(data).build();

		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<ServiceAdvisorAvailabilityResponse>> update(@PathVariable Long id,
			@Valid @RequestBody ServiceAdvisorAvailabilityRequest request) {

		ServiceAdvisorAvailabilityResponse data = availabilityService.update(id, request);

		ApiResponse<ServiceAdvisorAvailabilityResponse> response = ApiResponse
				.<ServiceAdvisorAvailabilityResponse>builder().status("SUCCESS")
				.message("Advisor availability updated successfully").data(data).build();

		return ResponseEntity.ok(response);
	}
}