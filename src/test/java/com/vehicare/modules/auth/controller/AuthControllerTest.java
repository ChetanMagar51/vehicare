package com.vehicare.modules.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicare.modules.auth.dto.LoginRequest;
import com.vehicare.modules.auth.dto.LoginResponse;
import com.vehicare.modules.auth.security.JwtService;
import com.vehicare.modules.auth.service.AuthService;
import com.vehicare.modules.user.dto.RegisterRequest;
import com.vehicare.modules.user.dto.UserDto;
import com.vehicare.modules.user.exception.EmailAlreadyExistsException;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

	@Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    
    @MockBean
    private JwtService jwtService;
    // =========================================================
    // REGISTER
    // =========================================================

    @Test
    void register_ShouldReturnCreated_WhenRequestIsValid() throws Exception {

        RegisterRequest request = RegisterRequest.builder()
                .firstName("Chetan")
                .lastName("Magar")
                .phone("9876543210")
                .address("Pune")
                .email("chetan@gmail.com")
                .password("password123")
                .build();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .firstName("Chetan")
                .lastName("Magar")
                .email("chetan@gmail.com")
                .phone("9876543210")
                .address("Pune")
                .build();

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(userDto);

        mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("Success"))
        .andExpect(jsonPath("$.message")
                .value("User registered successfully"))
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.firstName").value("Chetan"))
        .andExpect(jsonPath("$.data.email")
                .value("chetan@gmail.com"));

        verify(authService)
                .register(any(RegisterRequest.class));
    }
    
    @Test
    void register_ShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {

        RegisterRequest request = RegisterRequest.builder()
                .firstName("")
                .lastName("")
                .phone("")
                .address("")
                .email("invalid-email")
                .password("")
                .build();

        mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isBadRequest());

        verify(authService, never())
                .register(any(RegisterRequest.class));
    }
    
    
    @Test
    void register_ShouldReturnConflict_WhenEmailAlreadyExists() throws Exception {

        RegisterRequest request = RegisterRequest.builder()
                .firstName("Chetan")
                .lastName("Magar")
                .phone("9876543210")
                .address("Pune")
                .email("chetan@gmail.com")
                .password("password123")
                .build();

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new EmailAlreadyExistsException(
                        "User already exists with email: chetan@gmail.com"
                ));

        mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value("Failed"))
        .andExpect(jsonPath("$.message")
                .value("User already exists with email: chetan@gmail.com"))
        .andExpect(jsonPath("$.data").doesNotExist());

        verify(authService)
                .register(any(RegisterRequest.class));
    }
    
    
    //login
    @Test
    void login_ShouldReturnOk_WhenCredentialsAreValid() throws Exception {

        LoginRequest request = LoginRequest.builder()
                .email("chetan@gmail.com")
                .password("password123")
                .build();

        LoginResponse response = LoginResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .tokenType("Bearer")
                .build();

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken")
                .value("access-token"))
        .andExpect(jsonPath("$.refreshToken")
                .value("refresh-token"))
        .andExpect(jsonPath("$.tokenType")
                .value("Bearer"));

        verify(authService)
                .login(any(LoginRequest.class));
    }
    
    
    @Test
    void login_ShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {

        LoginRequest request = LoginRequest.builder()
                .email("")
                .password("")
                .build();

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isBadRequest());

        verify(authService, never())
                .login(any(LoginRequest.class));
    }
    
    
    
    @Test
    void login_ShouldReturnUnauthorized_WhenCredentialsAreInvalid() throws Exception {

        LoginRequest request = LoginRequest.builder()
                .email("chetan@gmail.com")
                .password("wrongpassword")
                .build();

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value("Failed"))
        .andExpect(jsonPath("$.message")
                .value("Invalid email or password"))
        .andExpect(jsonPath("$.data").doesNotExist());

        verify(authService)
                .login(any(LoginRequest.class));
    }
    
}