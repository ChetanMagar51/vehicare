package com.vehicare.modules.auth.service;



import com.vehicare.modules.auth.dto.LoginRequest;
import com.vehicare.modules.auth.dto.LoginResponse;
import com.vehicare.modules.user.dto.RegisterRequest;
import com.vehicare.modules.user.dto.UserDto;

public interface AuthService {

    UserDto register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}