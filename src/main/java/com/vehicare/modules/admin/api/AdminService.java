package com.vehicare.modules.admin.api;

import java.util.List;

import com.vehicare.modules.user.dto.RegisterRequest;
import com.vehicare.modules.user.dto.UserDto;

public interface AdminService {

	UserDto createOwner(RegisterRequest request);

    UserDto createServiceAdvisor(RegisterRequest request);

    UserDto createSubAdmin(RegisterRequest request);

    List<UserDto> getOwners();

    List<UserDto> getServiceAdvisors();

    List<UserDto> getSubAdmins();

    UserDto getUserById(Long id);

    void deleteUser(Long id);


}
