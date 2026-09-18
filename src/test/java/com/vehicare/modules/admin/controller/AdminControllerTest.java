package com.vehicare.modules.admin.controller;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicare.modules.admin.api.AdminService;
import com.vehicare.modules.auth.security.JwtService;
import com.vehicare.modules.user.dto.RegisterRequest;
import com.vehicare.modules.user.dto.UserDto;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;
    

    @MockBean
    private JwtService jwtService;

    private RegisterRequest createRegisterRequest() {
        return RegisterRequest.builder()
                .firstName("Chetan")
                .lastName("Magar")
                .phone("9876543210")
                .address("Pune")
                .email("chetan@gmail.com")
                .password("password123")
                .build();
    }

    private UserDto createUserDto() {
        return UserDto.builder()
                .id(1L)
                .firstName("Chetan")
                .lastName("Magar")
                .phone("9876543210")
                .address("Pune")
                .email("chetan@gmail.com")
                .build();
    }

    // GET OWNERS

    @Test
    void getOwners_ShouldReturnOwners() throws Exception {

        UserDto owner1 = createUserDto();

        UserDto owner2 = UserDto.builder()
                .id(2L)
                .firstName("Rahul")
                .lastName("Patil")
                .phone("9999999999")
                .address("Pune")
                .email("rahul@gmail.com")
                .build();

        when(adminService.getOwners())
                .thenReturn(Arrays.asList(owner1, owner2));

        mockMvc.perform(
                get("/admin/owners")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].firstName").value("Chetan"))
        .andExpect(jsonPath("$[0].email")
                .value("chetan@gmail.com"))
        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[1].firstName").value("Rahul"));

        verify(adminService, times(1))
                .getOwners();
    }

    @Test
    void getOwners_ShouldReturnEmptyList_WhenNoOwnersExist()
            throws Exception {

        when(adminService.getOwners())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(
                get("/admin/owners")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));

        verify(adminService, times(1))
                .getOwners();
    }

    // GET SERVICE ADVISORS

    @Test
    void getServiceAdvisors_ShouldReturnServiceAdvisors()
            throws Exception {

        UserDto advisor = UserDto.builder()
                .id(2L)
                .firstName("Rahul")
                .lastName("Patil")
                .phone("9999999999")
                .address("Pune")
                .email("rahul@gmail.com")
                .build();

        when(adminService.getServiceAdvisors())
                .thenReturn(Collections.singletonList(advisor));

        mockMvc.perform(
                get("/admin/service-advisors")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value(2))
        .andExpect(jsonPath("$[0].firstName")
                .value("Rahul"))
        .andExpect(jsonPath("$[0].email")
                .value("rahul@gmail.com"));

        verify(adminService, times(1))
                .getServiceAdvisors();
    }

    // GET SUB ADMINS

    @Test
    void getSubAdmins_ShouldReturnSubAdmins()
            throws Exception {

        UserDto subAdmin = UserDto.builder()
                .id(3L)
                .firstName("Amit")
                .lastName("Sharma")
                .phone("8888888888")
                .address("Pune")
                .email("amit@gmail.com")
                .build();

        when(adminService.getSubAdmins())
                .thenReturn(Collections.singletonList(subAdmin));

        mockMvc.perform(
                get("/admin/sub-admins")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value(3))
        .andExpect(jsonPath("$[0].firstName")
                .value("Amit"))
        .andExpect(jsonPath("$[0].email")
                .value("amit@gmail.com"));

        verify(adminService, times(1))
                .getSubAdmins();
    }

    // GET USER BY ID

    @Test
    void getUserById_ShouldReturnUser() throws Exception {

        Long id = 1L;

        when(adminService.getUserById(id))
                .thenReturn(createUserDto());

        mockMvc.perform(
                get("/admin/users/{id}", id)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.firstName")
                .value("Chetan"))
        .andExpect(jsonPath("$.lastName")
                .value("Magar"))
        .andExpect(jsonPath("$.email")
                .value("chetan@gmail.com"));

        verify(adminService, times(1))
                .getUserById(id);
    }

    // DELETE USER

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {

        Long id = 1L;

        mockMvc.perform(
                delete("/admin/users/{id}", id)
        )
        .andExpect(status().isNoContent());

        verify(adminService, times(1))
                .deleteUser(id);
    }

    // CREATE OWNER

    @Test
    void createOwner_ShouldReturnCreated_WhenRequestIsValid()
            throws Exception {

        RegisterRequest request = createRegisterRequest();

        UserDto userDto = createUserDto();

        when(adminService.createOwner(any(RegisterRequest.class)))
                .thenReturn(userDto);

        mockMvc.perform(
                post("/admin/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("Success"))
        .andExpect(jsonPath("$.message")
                .value("Owner created successfully"))
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.firstName")
                .value("Chetan"))
        .andExpect(jsonPath("$.data.email")
                .value("chetan@gmail.com"));

        verify(adminService, times(1))
                .createOwner(any(RegisterRequest.class));
    }

    // CREATE SERVICE ADVISOR

    @Test
    void createServiceAdvisor_ShouldReturnCreated_WhenRequestIsValid()
            throws Exception {

        RegisterRequest request = createRegisterRequest();

        UserDto userDto = createUserDto();

        when(adminService
                .createServiceAdvisor(any(RegisterRequest.class)))
                .thenReturn(userDto);

        mockMvc.perform(
                post("/admin/service-advisors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("Success"))
        .andExpect(jsonPath("$.message")
                .value("Service advisor created successfully"))
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.firstName")
                .value("Chetan"))
        .andExpect(jsonPath("$.data.email")
                .value("chetan@gmail.com"));

        verify(adminService, times(1))
                .createServiceAdvisor(any(RegisterRequest.class));
    }

    // CREATE SUB ADMIN

    @Test
    void createSubAdmin_ShouldReturnCreated_WhenRequestIsValid()
            throws Exception {

        RegisterRequest request = createRegisterRequest();

        UserDto userDto = createUserDto();

        when(adminService.createSubAdmin(any(RegisterRequest.class)))
                .thenReturn(userDto);

        mockMvc.perform(
                post("/admin/sub-admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("Success"))
        .andExpect(jsonPath("$.message")
                .value("Sub-admin created successfully"))
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.firstName")
                .value("Chetan"))
        .andExpect(jsonPath("$.data.email")
                .value("chetan@gmail.com"));

        verify(adminService, times(1))
                .createSubAdmin(any(RegisterRequest.class));
    }
}