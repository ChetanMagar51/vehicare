package com.vehicare.modules.serviceadvisor.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import com.vehicare.modules.serviceadvisor.api.ServiceAdvisorService;
import com.vehicare.modules.serviceadvisor.dto.ServiceAdvisorCreateRequest;
import com.vehicare.modules.serviceadvisor.dto.ServiceAdvisorResponse;
import com.vehicare.modules.serviceadvisor.dto.ServiceAdvisorUpdateRequest;

@RestController
@RequestMapping("/service-advisors")
@RequiredArgsConstructor
public class ServiceAdvisorController {

	private final ServiceAdvisorService serviceAdvisorService;

	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'SUB_ADMIN')")
	public ResponseEntity<ServiceAdvisorResponse> createProfile(
			@Valid @RequestBody ServiceAdvisorCreateRequest request) {

		ServiceAdvisorResponse response = serviceAdvisorService.createProfile(request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/user/{userId}")
	@PreAuthorize("""
			hasAnyRole('ADMIN', 'SUB_ADMIN')
			or (hasRole('SERVICE_ADVISOR')
			and @serviceAdvisorSecurity.isCurrentUser(#userId))
			""")
	public ResponseEntity<ServiceAdvisorResponse> getProfileByUserId(@PathVariable Long userId) {

		ServiceAdvisorResponse response = serviceAdvisorService.getProfileByUserId(userId);

		return ResponseEntity.ok(response);
	}

	@PutMapping("/user/{userId}")
	@PreAuthorize("""
			hasAnyRole('ADMIN', 'SUB_ADMIN')
			or (hasRole('SERVICE_ADVISOR')
			and @serviceAdvisorSecurity.isCurrentUser(#userId))
			""")
	public ResponseEntity<ServiceAdvisorResponse> updateProfile(@PathVariable Long userId,
			@Valid @RequestBody ServiceAdvisorUpdateRequest request) {

		ServiceAdvisorResponse response = serviceAdvisorService.updateProfile(userId, request);

		return ResponseEntity.ok(response);
	}
}