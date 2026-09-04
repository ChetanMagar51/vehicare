package com.vehicare.modules.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.vehicare.modules.auth.dto.LoginRequest;
import com.vehicare.modules.auth.dto.LoginResponse;
import com.vehicare.modules.auth.security.JwtService;
import com.vehicare.modules.auth.security.UserPrincipal;
import com.vehicare.modules.user.dto.RegisterRequest;
import com.vehicare.modules.user.dto.UserDto;
import com.vehicare.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public UserDto register(RegisterRequest request) {
        return userService.createUser(request);
    }

    @Override
    public LoginResponse login(LoginRequest request) {

       Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        UserPrincipal principal =
                (UserPrincipal) authentication.getPrincipal();

        String accessToken =
                jwtService.generateAccessToken(principal);

        String refreshToken =
                jwtService.generateRefreshToken(principal);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }
}