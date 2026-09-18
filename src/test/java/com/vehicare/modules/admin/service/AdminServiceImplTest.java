package com.vehicare.modules.admin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vehicare.modules.user.dto.RegisterRequest;
import com.vehicare.modules.user.dto.UserDto;
import com.vehicare.modules.user.entity.Role;
import com.vehicare.modules.user.service.UserService;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminServiceImpl adminService;

    private RegisterRequest registerRequest;
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

        userDto = UserDto.builder()
                .id(1L)
                .firstName("Chetan")
                .lastName("Magar")
                .phone("9876543210")
                .address("Pune")
                .email("chetan@gmail.com")
                .build();
    }

    // CREATE OWNER

    @Test
    void createOwner_ShouldCreateOwnerSuccessfully() {

        when(userService.createUser(registerRequest, Role.Owner))
                .thenReturn(userDto);

        UserDto result = adminService.createOwner(registerRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Chetan", result.getFirstName());
        assertEquals("Magar", result.getLastName());
        assertEquals("chetan@gmail.com", result.getEmail());

        verify(userService, times(1))
                .createUser(registerRequest, Role.Owner);
    }

    // CREATE SERVICE ADVISOR

    @Test
    void createServiceAdvisor_ShouldCreateServiceAdvisorSuccessfully() {

        when(userService.createUser(registerRequest, Role.Service_Adviser))
                .thenReturn(userDto);

        UserDto result = adminService.createServiceAdvisor(registerRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Chetan", result.getFirstName());
        assertEquals("Magar", result.getLastName());
        assertEquals("chetan@gmail.com", result.getEmail());

        verify(userService, times(1))
                .createUser(registerRequest, Role.Service_Adviser);
    }

    // CREATE SUB ADMIN

    @Test
    void createSubAdmin_ShouldCreateSubAdminSuccessfully() {

        when(userService.createUser(registerRequest, Role.Sub_Admin))
                .thenReturn(userDto);

        UserDto result = adminService.createSubAdmin(registerRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Chetan", result.getFirstName());
        assertEquals("Magar", result.getLastName());
        assertEquals("chetan@gmail.com", result.getEmail());

        verify(userService, times(1))
                .createUser(registerRequest, Role.Sub_Admin);
    }

    // GET OWNERS

    @Test
    void getOwners_ShouldReturnOwners() {

        UserDto owner1 = userDto;

        UserDto owner2 = UserDto.builder()
                .id(2L)
                .firstName("Rahul")
                .lastName("Patil")
                .email("rahul@gmail.com")
                .phone("9999999999")
                .address("Pune")
                .build();

        when(userService.getUsersByRole(Role.Owner))
                .thenReturn(Arrays.asList(owner1, owner2));

        List<UserDto> result = adminService.getOwners();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Chetan", result.get(0).getFirstName());
        assertEquals("Rahul", result.get(1).getFirstName());

        verify(userService, times(1))
                .getUsersByRole(Role.Owner);
    }

    // GET SERVICE ADVISORS

    @Test
    void getServiceAdvisors_ShouldReturnServiceAdvisors() {

        UserDto advisor = UserDto.builder()
                .id(2L)
                .firstName("Rahul")
                .lastName("Patil")
                .email("rahul@gmail.com")
                .phone("9999999999")
                .address("Pune")
                .build();

        when(userService.getUsersByRole(Role.Service_Adviser))
                .thenReturn(Collections.singletonList(advisor));

        List<UserDto> result = adminService.getServiceAdvisors();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Rahul", result.get(0).getFirstName());

        verify(userService, times(1))
                .getUsersByRole(Role.Service_Adviser);
    }

    // GET SUB ADMINS

    @Test
    void getSubAdmins_ShouldReturnSubAdmins() {

        UserDto subAdmin = UserDto.builder()
                .id(3L)
                .firstName("Amit")
                .lastName("Sharma")
                .email("amit@gmail.com")
                .phone("8888888888")
                .address("Pune")
                .build();

        when(userService.getUsersByRole(Role.Sub_Admin))
                .thenReturn(Collections.singletonList(subAdmin));

        List<UserDto> result = adminService.getSubAdmins();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Amit", result.get(0).getFirstName());

        verify(userService, times(1))
                .getUsersByRole(Role.Sub_Admin);
    }

    // GET USER BY ID

    @Test
    void getUserById_ShouldReturnUser() {

        Long id = 1L;

        when(userService.getUserById(id))
                .thenReturn(userDto);

        UserDto result = adminService.getUserById(id);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Chetan", result.getFirstName());
        assertEquals("chetan@gmail.com", result.getEmail());

        verify(userService, times(1))
                .getUserById(id);
    }

    // DELETE USER

    @Test
    void deleteUser_ShouldDeleteUser() {

        Long id = 1L;

        adminService.deleteUser(id);

        verify(userService, times(1))
                .deleteUser(id);
    }
}