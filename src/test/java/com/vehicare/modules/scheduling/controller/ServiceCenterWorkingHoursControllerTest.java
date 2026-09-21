package com.vehicare.modules.scheduling.controller;

import static org.mockito.ArgumentMatchers.any;
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
import com.vehicare.modules.scheduling.dto.ServiceCenterWorkingHoursRequest;
import com.vehicare.modules.scheduling.dto.ServiceCenterWorkingHoursResponse;
import com.vehicare.modules.scheduling.service.ServiceCenterWorkingHoursServiceImpl;

@WebMvcTest(ServiceCenterWorkingHoursController.class)
@AutoConfigureMockMvc(addFilters = false)
class ServiceCenterWorkingHoursControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ServiceCenterWorkingHoursServiceImpl workingHoursService;
    
    @MockBean
    private JwtService jwtService;

    // ---------------------------------------------------------
    // POST /scheduling/working-hours
    // ---------------------------------------------------------

    @Test
    void create_shouldReturnCreatedSuccessfully() throws Exception {

        ServiceCenterWorkingHoursRequest request =
                ServiceCenterWorkingHoursRequest.builder()
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .openingTime(LocalTime.of(9, 0))
                        .closingTime(LocalTime.of(18, 0))
                        .breakStart(LocalTime.of(13, 0))
                        .breakEnd(LocalTime.of(14, 0))
                        .closed(false)
                        .build();

        ServiceCenterWorkingHoursResponse response =
                ServiceCenterWorkingHoursResponse.builder()
                        .id(1L)
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .openingTime(LocalTime.of(9, 0))
                        .closingTime(LocalTime.of(18, 0))
                        .breakStart(LocalTime.of(13, 0))
                        .breakEnd(LocalTime.of(14, 0))
                        .closed(false)
                        .build();

        when(workingHoursService.create(any(ServiceCenterWorkingHoursRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/scheduling/working-hours")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message")
                        .value("Working hours created successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.dayOfWeek").value("MONDAY"))
                .andExpect(jsonPath("$.data.openingTime").value("09:00:00"))
                .andExpect(jsonPath("$.data.closingTime").value("18:00:00"))
                .andExpect(jsonPath("$.data.breakStart").value("13:00:00"))
                .andExpect(jsonPath("$.data.breakEnd").value("14:00:00"))
                .andExpect(jsonPath("$.data.closed").value(false));

        verify(workingHoursService).create(any(ServiceCenterWorkingHoursRequest.class));
    }

    // ---------------------------------------------------------
    // GET /scheduling/working-hours
    // ---------------------------------------------------------

    @Test
    void getAll_shouldReturnWorkingHoursSuccessfully() throws Exception {

        ServiceCenterWorkingHoursResponse monday =
                ServiceCenterWorkingHoursResponse.builder()
                        .id(1L)
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .openingTime(LocalTime.of(9, 0))
                        .closingTime(LocalTime.of(18, 0))
                        .breakStart(LocalTime.of(13, 0))
                        .breakEnd(LocalTime.of(14, 0))
                        .closed(false)
                        .build();

        ServiceCenterWorkingHoursResponse tuesday =
                ServiceCenterWorkingHoursResponse.builder()
                        .id(2L)
                        .dayOfWeek(DayOfWeek.TUESDAY)
                        .openingTime(LocalTime.of(9, 0))
                        .closingTime(LocalTime.of(18, 0))
                        .breakStart(LocalTime.of(13, 0))
                        .breakEnd(LocalTime.of(14, 0))
                        .closed(false)
                        .build();

        when(workingHoursService.getAll())
                .thenReturn(List.of(monday, tuesday));

        mockMvc.perform(get("/scheduling/working-hours"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message")
                        .value("Working hours retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].dayOfWeek").value("MONDAY"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].dayOfWeek").value("TUESDAY"));

        verify(workingHoursService).getAll();
    }

    // ---------------------------------------------------------
    // GET /scheduling/working-hours/{dayOfWeek}
    // ---------------------------------------------------------

    @Test
    void getByDay_shouldReturnWorkingHoursSuccessfully() throws Exception {

        ServiceCenterWorkingHoursResponse response =
                ServiceCenterWorkingHoursResponse.builder()
                        .id(1L)
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .openingTime(LocalTime.of(9, 0))
                        .closingTime(LocalTime.of(18, 0))
                        .breakStart(LocalTime.of(13, 0))
                        .breakEnd(LocalTime.of(14, 0))
                        .closed(false)
                        .build();

        when(workingHoursService.getByDay(DayOfWeek.MONDAY))
                .thenReturn(response);

        mockMvc.perform(get("/scheduling/working-hours/MONDAY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message")
                        .value("Working hours retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.dayOfWeek").value("MONDAY"))
                .andExpect(jsonPath("$.data.openingTime").value("09:00:00"))
                .andExpect(jsonPath("$.data.closingTime").value("18:00:00"))
                .andExpect(jsonPath("$.data.closed").value(false));

        verify(workingHoursService).getByDay(DayOfWeek.MONDAY);
    }

    // ---------------------------------------------------------
    // PUT /scheduling/working-hours/{dayOfWeek}
    // ---------------------------------------------------------

    @Test
    void update_shouldReturnUpdatedWorkingHoursSuccessfully() throws Exception {

        ServiceCenterWorkingHoursRequest request =
                ServiceCenterWorkingHoursRequest.builder()
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .openingTime(LocalTime.of(8, 30))
                        .closingTime(LocalTime.of(19, 0))
                        .breakStart(LocalTime.of(13, 0))
                        .breakEnd(LocalTime.of(14, 0))
                        .closed(false)
                        .build();

        ServiceCenterWorkingHoursResponse response =
                ServiceCenterWorkingHoursResponse.builder()
                        .id(1L)
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .openingTime(LocalTime.of(8, 30))
                        .closingTime(LocalTime.of(19, 0))
                        .breakStart(LocalTime.of(13, 0))
                        .breakEnd(LocalTime.of(14, 0))
                        .closed(false)
                        .build();

        when(workingHoursService.update(
                any(DayOfWeek.class),
                any(ServiceCenterWorkingHoursRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/scheduling/working-hours/MONDAY")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message")
                        .value("Working hours updated successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.dayOfWeek").value("MONDAY"))
                .andExpect(jsonPath("$.data.openingTime").value("08:30:00"))
                .andExpect(jsonPath("$.data.closingTime").value("19:00:00"));

        verify(workingHoursService).update(
                any(DayOfWeek.class),
                any(ServiceCenterWorkingHoursRequest.class));
    }
}