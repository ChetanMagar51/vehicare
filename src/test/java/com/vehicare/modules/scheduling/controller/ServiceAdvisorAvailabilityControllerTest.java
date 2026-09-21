package com.vehicare.modules.scheduling.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicare.modules.auth.security.JwtService;
import com.vehicare.modules.scheduling.api.ServiceAdvisorAvailabilityService;
import com.vehicare.modules.scheduling.dto.ServiceAdvisorAvailabilityRequest;
import com.vehicare.modules.scheduling.dto.ServiceAdvisorAvailabilityResponse;

@WebMvcTest(ServiceAdvisorAvailabilityController.class)
@AutoConfigureMockMvc(addFilters = false)
class ServiceAdvisorAvailabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ServiceAdvisorAvailabilityService availabilityService;
    
    @MockBean
    private JwtService jwtService;

    private ServiceAdvisorAvailabilityRequest validRequest() {

        return ServiceAdvisorAvailabilityRequest.builder()
                .serviceAdvisorId(1L)
                .dayOfWeek(DayOfWeek.MONDAY)
                .availableFrom(LocalTime.of(9, 0))
                .availableTo(LocalTime.of(13, 0))
                .available(true)
                .build();
    }

    private ServiceAdvisorAvailabilityResponse validResponse() {

        return ServiceAdvisorAvailabilityResponse.builder()
                .id(10L)
                .serviceAdvisorId(1L)
                .dayOfWeek(DayOfWeek.MONDAY)
                .availableFrom(LocalTime.of(9, 0))
                .availableTo(LocalTime.of(13, 0))
                .available(true)
                .build();
    }

    // ---------------------------------------------------------
    // POST /scheduling/advisor-availability
    // ---------------------------------------------------------

    @Test
    void create_shouldReturnCreatedSuccessfully() throws Exception {

        when(availabilityService.create(
                any(ServiceAdvisorAvailabilityRequest.class)))
                .thenReturn(validResponse());

        mockMvc.perform(post("/scheduling/advisor-availability")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message")
                        .value("Advisor availability created successfully"))
                .andExpect(jsonPath("$.data.id").value(10))
                .andExpect(jsonPath("$.data.serviceAdvisorId").value(1))
                .andExpect(jsonPath("$.data.dayOfWeek").value("MONDAY"))
                .andExpect(jsonPath("$.data.availableFrom").value("09:00:00"))
                .andExpect(jsonPath("$.data.availableTo").value("13:00:00"))
                .andExpect(jsonPath("$.data.available").value(true));

        verify(availabilityService).create(
                any(ServiceAdvisorAvailabilityRequest.class));
    }

    // ---------------------------------------------------------
    // GET /scheduling/advisor-availability/{id}
    // ---------------------------------------------------------

    @Test
    void getById_shouldReturnAvailabilitySuccessfully() throws Exception {

        when(availabilityService.getById(10L))
                .thenReturn(validResponse());

        mockMvc.perform(get("/scheduling/advisor-availability/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message")
                        .value("Advisor availability retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(10))
                .andExpect(jsonPath("$.data.serviceAdvisorId").value(1))
                .andExpect(jsonPath("$.data.dayOfWeek").value("MONDAY"))
                .andExpect(jsonPath("$.data.availableFrom").value("09:00:00"))
                .andExpect(jsonPath("$.data.availableTo").value("13:00:00"))
                .andExpect(jsonPath("$.data.available").value(true));

        verify(availabilityService).getById(10L);
    }

    // ---------------------------------------------------------
    // GET /scheduling/advisor-availability/advisor/{serviceAdvisorId}
    // ---------------------------------------------------------

    @Test
    void getByAdvisorId_shouldReturnAvailabilitiesSuccessfully()
            throws Exception {

        ServiceAdvisorAvailabilityResponse monday =
                validResponse();

        ServiceAdvisorAvailabilityResponse tuesday =
                ServiceAdvisorAvailabilityResponse.builder()
                        .id(11L)
                        .serviceAdvisorId(1L)
                        .dayOfWeek(DayOfWeek.TUESDAY)
                        .availableFrom(LocalTime.of(10, 0))
                        .availableTo(LocalTime.of(14, 0))
                        .available(true)
                        .build();

        when(availabilityService.getByAdvisorId(1L))
                .thenReturn(List.of(monday, tuesday));

        mockMvc.perform(
                get("/scheduling/advisor-availability/advisor/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message")
                        .value("Advisor availability retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(10))
                .andExpect(jsonPath("$.data[0].dayOfWeek").value("MONDAY"))
                .andExpect(jsonPath("$.data[1].id").value(11))
                .andExpect(jsonPath("$.data[1].dayOfWeek").value("TUESDAY"));

        verify(availabilityService).getByAdvisorId(1L);
    }

    // ---------------------------------------------------------
    // GET /scheduling/advisor-availability/advisor/{id}/{day}
    // ---------------------------------------------------------

    @Test
    void getByAdvisorIdAndDay_shouldReturnAvailabilitySuccessfully()
            throws Exception {

        when(availabilityService.getByAdvisorIdAndDay(
                1L, DayOfWeek.MONDAY))
                .thenReturn(List.of(validResponse()));

        mockMvc.perform(
                get("/scheduling/advisor-availability/advisor/1/MONDAY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message")
                        .value("Advisor availability retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(10))
                .andExpect(jsonPath("$.data[0].serviceAdvisorId").value(1))
                .andExpect(jsonPath("$.data[0].dayOfWeek").value("MONDAY"));

        verify(availabilityService).getByAdvisorIdAndDay(
                1L, DayOfWeek.MONDAY);
    }

    // ---------------------------------------------------------
    // PUT /scheduling/advisor-availability/{id}
    // ---------------------------------------------------------

    @Test
    void update_shouldReturnUpdatedAvailabilitySuccessfully()
            throws Exception {

        ServiceAdvisorAvailabilityRequest request =
                ServiceAdvisorAvailabilityRequest.builder()
                        .serviceAdvisorId(1L)
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .availableFrom(LocalTime.of(10, 0))
                        .availableTo(LocalTime.of(14, 0))
                        .available(true)
                        .build();

        ServiceAdvisorAvailabilityResponse response =
                ServiceAdvisorAvailabilityResponse.builder()
                        .id(10L)
                        .serviceAdvisorId(1L)
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .availableFrom(LocalTime.of(10, 0))
                        .availableTo(LocalTime.of(14, 0))
                        .available(true)
                        .build();

        when(availabilityService.update(
                anyLong(),
                any(ServiceAdvisorAvailabilityRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/scheduling/advisor-availability/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message")
                        .value("Advisor availability updated successfully"))
                .andExpect(jsonPath("$.data.id").value(10))
                .andExpect(jsonPath("$.data.serviceAdvisorId").value(1))
                .andExpect(jsonPath("$.data.dayOfWeek").value("MONDAY"))
                .andExpect(jsonPath("$.data.availableFrom").value("10:00:00"))
                .andExpect(jsonPath("$.data.availableTo").value("14:00:00"))
                .andExpect(jsonPath("$.data.available").value(true));

        verify(availabilityService).update(
                anyLong(),
                any(ServiceAdvisorAvailabilityRequest.class));
    }
}