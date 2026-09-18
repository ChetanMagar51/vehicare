package com.vehicare.modules.user.service;

import java.util.List;

import com.vehicare.modules.user.dto.RegisterRequest;
import com.vehicare.modules.user.dto.UserDto;
import com.vehicare.modules.user.entity.Role;


public interface UserService {

	UserDto createUser(RegisterRequest request);

    UserDto getUserById(Long id);


    List<UserDto> getAllUsers();


    List<UserDto> getUsersByRole(Role role);

    void deleteUser(Long id);

    UserDto createUser(RegisterRequest request, Role role);



}
