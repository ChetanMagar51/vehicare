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
import com.vehicare.modules.scheduling.api.ServiceCenterWorkingHoursService;
import com.vehicare.modules.scheduling.dto.ServiceCenterWorkingHoursRequest;
import com.vehicare.modules.scheduling.dto.ServiceCenterWorkingHoursResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/scheduling/working-hours")
@RequiredArgsConstructor
public class ServiceCenterWorkingHoursController {

    private final ServiceCenterWorkingHoursService workingHoursService;

    @PostMapping
    public ResponseEntity<ApiResponse<ServiceCenterWorkingHoursResponse>> create(
            @Valid @RequestBody ServiceCenterWorkingHoursRequest request) {

        ServiceCenterWorkingHoursResponse data =
                workingHoursService.create(request);

        ApiResponse<ServiceCenterWorkingHoursResponse> response =
                ApiResponse.<ServiceCenterWorkingHoursResponse>builder()
                        .status("SUCCESS")
                        .message("Working hours created successfully")
                        .data(data)
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ServiceCenterWorkingHoursResponse>>> getAll() {

        List<ServiceCenterWorkingHoursResponse> data =
                workingHoursService.getAll();

        ApiResponse<List<ServiceCenterWorkingHoursResponse>> response =
                ApiResponse.<List<ServiceCenterWorkingHoursResponse>>builder()
                        .status("SUCCESS")
                        .message("Working hours retrieved successfully")
                        .data(data)
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{dayOfWeek}")
    public ResponseEntity<ApiResponse<ServiceCenterWorkingHoursResponse>> getByDay(
            @PathVariable DayOfWeek dayOfWeek) {

        ServiceCenterWorkingHoursResponse data =
                workingHoursService.getByDay(dayOfWeek);

        ApiResponse<ServiceCenterWorkingHoursResponse> response =
                ApiResponse.<ServiceCenterWorkingHoursResponse>builder()
                        .status("SUCCESS")
                        .message("Working hours retrieved successfully")
                        .data(data)
                        .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{dayOfWeek}")
    public ResponseEntity<ApiResponse<ServiceCenterWorkingHoursResponse>> update(
            @PathVariable DayOfWeek dayOfWeek,
            @Valid @RequestBody ServiceCenterWorkingHoursRequest request) {

        ServiceCenterWorkingHoursResponse data =
                workingHoursService.update(dayOfWeek, request);

        ApiResponse<ServiceCenterWorkingHoursResponse> response =
                ApiResponse.<ServiceCenterWorkingHoursResponse>builder()
                        .status("SUCCESS")
                        .message("Working hours updated successfully")
                        .data(data)
                        .build();

        return ResponseEntity.ok(response);
    }
}