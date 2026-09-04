package com.vehicare.modules.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vehicare.modules.admin.api.AdminService;
import com.vehicare.modules.user.dto.RegisterRequest;
import com.vehicare.modules.user.dto.UserDto;
import com.vehicare.modules.user.entity.Role;
import com.vehicare.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
	

    private final UserService userService;
    
    
    @Override
    public UserDto createOwner(RegisterRequest request) {
        return userService.createUser(request, Role.Owner);
    }

    @Override
    public UserDto createServiceAdvisor(RegisterRequest request) {
        return userService.createUser(request, Role.Service_Adviser);
    }

    @Override
    public UserDto createSubAdmin(RegisterRequest request) {
        return userService.createUser(request, Role.Sub_Admin);
    }

    @Override
    public List<UserDto> getOwners() {
        return userService.getUsersByRole(Role.Owner);
    }

    @Override
    public List<UserDto> getServiceAdvisors() {
        return userService.getUsersByRole(Role.Service_Adviser);
    }

    @Override
    public List<UserDto> getSubAdmins() {
        return userService.getUsersByRole(Role.Sub_Admin);
    }

    @Override
    public UserDto getUserById(Long id) {
        return userService.getUserById(id);
    }

    @Override
    public void deleteUser(Long id) {
        userService.deleteUser(id);
    }
    
    

}
