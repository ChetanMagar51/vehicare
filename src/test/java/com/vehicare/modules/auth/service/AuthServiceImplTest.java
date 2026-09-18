package com.vehicare.modules.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.vehicare.modules.auth.dto.LoginRequest;
import com.vehicare.modules.auth.dto.LoginResponse;
import com.vehicare.modules.auth.security.JwtService;
import com.vehicare.modules.auth.security.UserPrincipal;
import com.vehicare.modules.user.dto.RegisterRequest;
import com.vehicare.modules.user.dto.UserDto;
import com.vehicare.modules.user.service.UserService;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    @Mock
    private UserPrincipal userPrincipal;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private UserDto userDto;

    @BeforeEach
    void setUp() {

        registerRequest = RegisterRequest.builder()
                .firstName("Chetan")
                .lastName("Magar")
                .phone("9876543210")
                .address("Pune")
                .email("chetan@gmail.com")
                .password("password123")
                .build();

        loginRequest = LoginRequest.builder()
                .email("chetan@gmail.com")
                .password("password123")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .firstName("Chetan")
                .lastName("Magar")
                .email("chetan@gmail.com")
                .phone("9876543210")
                .address("Pune")
                .build();
    }


    // =========================================================
    // register()
    // =========================================================

    @Test
    void register_ShouldReturnUserDto_WhenRegistrationIsSuccessful() {

        when(userService.createUser(registerRequest))
                .thenReturn(userDto);

        UserDto result = authService.register(registerRequest);

        assertNotNull(result);

        assertEquals(1L, result.getId());
        assertEquals("Chetan", result.getFirstName());
        assertEquals("chetan@gmail.com", result.getEmail());

        verify(userService)
                .createUser(registerRequest);
    }


    @Test
    void register_ShouldThrowException_WhenUserServiceFails() {

        when(userService.createUser(registerRequest))
                .thenThrow(new RuntimeException("Email already exists"));

        assertThrows(
                RuntimeException.class,
                () -> authService.register(registerRequest)
        );

        verify(userService)
                .createUser(registerRequest);
    }


    // =========================================================
    // login()
    // =========================================================

    @Test
    void login_ShouldReturnLoginResponse_WhenAuthenticationIsSuccessful() {

        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        when(authenticationManager.authenticate(any(
                UsernamePasswordAuthenticationToken.class
        ))).thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(userPrincipal);

        when(jwtService.generateAccessToken(userPrincipal))
                .thenReturn(accessToken);

        when(jwtService.generateRefreshToken(userPrincipal))
                .thenReturn(refreshToken);

        LoginResponse result = authService.login(loginRequest);

        assertNotNull(result);

        assertEquals(accessToken, result.getAccessToken());
        assertEquals(refreshToken, result.getRefreshToken());
        assertEquals("Bearer", result.getTokenType());

        verify(authenticationManager)
                .authenticate(any(
                        UsernamePasswordAuthenticationToken.class
                ));

        verify(authentication)
                .getPrincipal();

        verify(jwtService)
                .generateAccessToken(userPrincipal);

        verify(jwtService)
                .generateRefreshToken(userPrincipal);
    }


    @Test
    void login_ShouldThrowException_WhenAuthenticationFails() {

        when(authenticationManager.authenticate(any(
                UsernamePasswordAuthenticationToken.class
        ))).thenThrow(new RuntimeException("Invalid credentials"));

        assertThrows(
                RuntimeException.class,
                () -> authService.login(loginRequest)
        );

        verify(authenticationManager)
                .authenticate(any(
                        UsernamePasswordAuthenticationToken.class
                ));

        verify(jwtService, never())
                .generateAccessToken(any(UserPrincipal.class));

        verify(jwtService, never())
                .generateRefreshToken(any(UserPrincipal.class));
    }
}