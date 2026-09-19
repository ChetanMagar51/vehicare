package com.vehicare.modules.serviceadvisor.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicare.modules.auth.security.JwtService;
import com.vehicare.modules.serviceadvisor.api.ServiceAdvisorService;
import com.vehicare.modules.serviceadvisor.dto.ServiceAdvisorCreateRequest;
import com.vehicare.modules.serviceadvisor.dto.ServiceAdvisorResponse;
import com.vehicare.modules.serviceadvisor.dto.ServiceAdvisorUpdateRequest;

@WebMvcTest(ServiceAdvisorController.class)
@AutoConfigureMockMvc(addFilters = false)
class ServiceAdvisorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ServiceAdvisorService serviceAdvisorService;

    @MockBean
    private JwtService jwtService;

    // --------------------------------------------------
    // POST /service-advisors
    // --------------------------------------------------

    @Test
    void createProfile_ShouldReturnCreated_WhenValidRequest() throws Exception {

        ServiceAdvisorCreateRequest request =
                ServiceAdvisorCreateRequest.builder()
                        .userId(1L)
                        .employeeId("SA001")
                        .specialization("Engine")
                        .experience(5)
                        .available(true)
                        .build();

        ServiceAdvisorResponse response =
                ServiceAdvisorResponse.builder()
                        .id(10L)
                        .userId(1L)
                        .employeeId("SA001")
                        .specialization("Engine")
                        .experience(5)
                        .available(true)
                        .build();

        when(serviceAdvisorService.createProfile(any(ServiceAdvisorCreateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/service-advisors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.employeeId").value("SA001"))
                .andExpect(jsonPath("$.specialization").value("Engine"))
                .andExpect(jsonPath("$.experience").value(5))
                .andExpect(jsonPath("$.available").value(true));
    }

    // --------------------------------------------------
    // GET /service-advisors/user/{userId}
    // --------------------------------------------------

    @Test
    void getProfileByUserId_ShouldReturnOk_WhenProfileExists() throws Exception {

        ServiceAdvisorResponse response =
                ServiceAdvisorResponse.builder()
                        .id(10L)
                        .userId(1L)
                        .employeeId("SA001")
                        .specialization("Engine")
                        .experience(5)
                        .available(true)
                        .build();

        when(serviceAdvisorService.getProfileByUserId(1L))
                .thenReturn(response);

        mockMvc.perform(get("/service-advisors/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.employeeId").value("SA001"))
                .andExpect(jsonPath("$.specialization").value("Engine"))
                .andExpect(jsonPath("$.experience").value(5))
                .andExpect(jsonPath("$.available").value(true));
    }

    // --------------------------------------------------
    // PUT /service-advisors/user/{userId}
    // --------------------------------------------------

    @Test
    void updateProfile_ShouldReturnOk_WhenValidRequest() throws Exception {

        ServiceAdvisorUpdateRequest request =
                ServiceAdvisorUpdateRequest.builder()
                        .specialization("Transmission")
                        .experience(7)
                        .available(false)
                        .build();

        ServiceAdvisorResponse response =
                ServiceAdvisorResponse.builder()
                        .id(10L)
                        .userId(1L)
                        .employeeId("SA001")
                        .specialization("Transmission")
                        .experience(7)
                        .available(false)
                        .build();

        when(serviceAdvisorService.updateProfile(
                any(Long.class),
                any(ServiceAdvisorUpdateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/service-advisors/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.employeeId").value("SA001"))
                .andExpect(jsonPath("$.specialization").value("Transmission"))
                .andExpect(jsonPath("$.experience").value(7))
                .andExpect(jsonPath("$.available").value(false));
    }
}
