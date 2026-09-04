package com.vehicare.modules.admin.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vehicare.common.api.ApiResponse;
import com.vehicare.modules.admin.api.AdminService;
import com.vehicare.modules.user.dto.RegisterRequest;
import com.vehicare.modules.user.dto.UserDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/owners")
    public ResponseEntity<List<UserDto>> getOwners() {
        return ResponseEntity.ok(adminService.getOwners());
    }

    @GetMapping("/service-advisors")
    public ResponseEntity<List<UserDto>> getServiceAdvisors() {
        return ResponseEntity.ok(adminService.getServiceAdvisors());
    }

    @GetMapping("/sub-admins")
    public ResponseEntity<List<UserDto>> getSubAdmins() {
        return ResponseEntity.ok(adminService.getSubAdmins());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUserById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                adminService.getUserById(id)
        );
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id) {

        adminService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/owners")
    public ResponseEntity<ApiResponse<UserDto>> createOwner(
            @Valid @RequestBody RegisterRequest request) {

        UserDto user = adminService.createOwner(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.<UserDto>builder()
                        .status("Success")
                        .message("Owner created successfully")
                        .data(user)
                        .build());
    }
    
    @PostMapping("/service-advisors")
    public ResponseEntity<ApiResponse<UserDto>> createServiceAdvisor(
            @Valid @RequestBody RegisterRequest request) {

        UserDto user = adminService.createServiceAdvisor(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.<UserDto>builder()
                        .status("Success")
                        .message("Service advisor created successfully")
                        .data(user)
                        .build());
    }
    
    @PostMapping("/sub-admins")
    public ResponseEntity<ApiResponse<UserDto>> createSubAdmin(
            @Valid @RequestBody RegisterRequest request) {

        UserDto user = adminService.createSubAdmin(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.<UserDto>builder()
                        .status("Success")
                        .message("Sub-admin created successfully")
                        .data(user)
                        .build());
    }
}