package com.vehicare.modules.user.service;

import java.util.List;

import com.vehicare.modules.user.dto.RegisterRequest;
import com.vehicare.modules.user.dto.UpdateUserRequest;
import com.vehicare.modules.user.dto.UserDto;
import com.vehicare.modules.user.entity.Role;

public interface UserService {

	UserDto createUser(RegisterRequest request);

	UserDto createUser(RegisterRequest request, Role role);

	UserDto getUserById(Long id);

	UserDto getUserByEmail(String email);

	List<UserDto> getAllUsers();

	List<UserDto> getUsersByRole(Role role);

	void deleteUser(Long id);

	UserDto updateUser(Long id, UpdateUserRequest request);

	void enableUser(Long id);

	void disableUser(Long id);

}
