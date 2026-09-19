package com.vehicare.modules.serviceadvisor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vehicare.modules.serviceadvisor.dto.ServiceAdvisorCreateRequest;
import com.vehicare.modules.serviceadvisor.dto.ServiceAdvisorResponse;
import com.vehicare.modules.serviceadvisor.dto.ServiceAdvisorUpdateRequest;
import com.vehicare.modules.serviceadvisor.entity.ServiceAdvisorProfile;
import com.vehicare.modules.serviceadvisor.exception.ServiceAdvisorProfileAlreadyExistsException;
import com.vehicare.modules.serviceadvisor.exception.ServiceAdvisorProfileNotFoundException;
import com.vehicare.modules.serviceadvisor.repository.ServiceAdvisorProfileRepository;

@ExtendWith(MockitoExtension.class)
class ServiceAdvisorServiceImplTest {

    @Mock
    private ServiceAdvisorProfileRepository profileRepository;

    @InjectMocks
    private ServiceAdvisorServiceImpl serviceAdvisorService;

    private ServiceAdvisorCreateRequest createRequest;
    private ServiceAdvisorProfile profile;

    @BeforeEach
    void setUp() {

        createRequest = ServiceAdvisorCreateRequest.builder()
                .userId(1L)
                .employeeId("SA001")
                .specialization("Engine")
                .experience(5)
                .available(true)
                .build();

        profile = ServiceAdvisorProfile.builder()
                .id(10L)
                .userId(1L)
                .employeeId("SA001")
                .specialization("Engine")
                .experience(5)
                .available(true)
                .build();
    }

    // --------------------------------------------------
    // createProfile()
    // --------------------------------------------------

    @Test
    void createProfile_ShouldCreateProfile_WhenValidRequest() {

        when(profileRepository.existsByUserId(1L))
                .thenReturn(false);

        when(profileRepository.existsByEmployeeId("SA001"))
                .thenReturn(false);

        when(profileRepository.save(any(ServiceAdvisorProfile.class)))
                .thenReturn(profile);

        ServiceAdvisorResponse response =
                serviceAdvisorService.createProfile(createRequest);

        assertEquals(10L, response.getId());
        assertEquals(1L, response.getUserId());
        assertEquals("SA001", response.getEmployeeId());
        assertEquals("Engine", response.getSpecialization());
        assertEquals(5, response.getExperience());
        assertTrue(response.getAvailable());

        verify(profileRepository).existsByUserId(1L);
        verify(profileRepository).existsByEmployeeId("SA001");
        verify(profileRepository).save(any(ServiceAdvisorProfile.class));
    }

    @Test
    void createProfile_ShouldThrowException_WhenUserProfileAlreadyExists() {

        when(profileRepository.existsByUserId(1L))
                .thenReturn(true);

        assertThrows(
                ServiceAdvisorProfileAlreadyExistsException.class,
                () -> serviceAdvisorService.createProfile(createRequest)
        );

        verify(profileRepository).existsByUserId(1L);

        // Employee ID check should not happen
        verify(profileRepository, never())
                .existsByEmployeeId(any());

        // Profile should not be saved
        verify(profileRepository, never())
                .save(any(ServiceAdvisorProfile.class));
    }

    @Test
    void createProfile_ShouldThrowException_WhenEmployeeIdAlreadyExists() {

        when(profileRepository.existsByUserId(1L))
                .thenReturn(false);

        when(profileRepository.existsByEmployeeId("SA001"))
                .thenReturn(true);

        assertThrows(
                ServiceAdvisorProfileAlreadyExistsException.class,
                () -> serviceAdvisorService.createProfile(createRequest)
        );

        verify(profileRepository).existsByUserId(1L);
        verify(profileRepository).existsByEmployeeId("SA001");

        // Profile should not be saved
        verify(profileRepository, never())
                .save(any(ServiceAdvisorProfile.class));
    }

    @Test
    void createProfile_ShouldSetAvailableTrue_WhenAvailableIsNull() {

        createRequest = ServiceAdvisorCreateRequest.builder()
                .userId(1L)
                .employeeId("SA002")
                .specialization("Brake")
                .experience(3)
                .available(null)
                .build();

        when(profileRepository.existsByUserId(1L))
                .thenReturn(false);

        when(profileRepository.existsByEmployeeId("SA002"))
                .thenReturn(false);

        ServiceAdvisorProfile savedProfile = ServiceAdvisorProfile.builder()
                .id(11L)
                .userId(1L)
                .employeeId("SA002")
                .specialization("Brake")
                .experience(3)
                .available(true)
                .build();

        when(profileRepository.save(any(ServiceAdvisorProfile.class)))
                .thenReturn(savedProfile);

        ServiceAdvisorResponse response =
                serviceAdvisorService.createProfile(createRequest);

        assertTrue(response.getAvailable());

        verify(profileRepository).save(any(ServiceAdvisorProfile.class));
    }

    // --------------------------------------------------
    // getProfileByUserId()
    // --------------------------------------------------

    @Test
    void getProfileByUserId_ShouldReturnProfile_WhenProfileExists() {

        when(profileRepository.findByUserId(1L))
                .thenReturn(Optional.of(profile));

        ServiceAdvisorResponse response =
                serviceAdvisorService.getProfileByUserId(1L);

        assertEquals(10L, response.getId());
        assertEquals(1L, response.getUserId());
        assertEquals("SA001", response.getEmployeeId());
        assertEquals("Engine", response.getSpecialization());
        assertEquals(5, response.getExperience());
        assertTrue(response.getAvailable());

        verify(profileRepository).findByUserId(1L);
    }

    @Test
    void getProfileByUserId_ShouldThrowException_WhenProfileDoesNotExist() {

        when(profileRepository.findByUserId(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ServiceAdvisorProfileNotFoundException.class,
                () -> serviceAdvisorService.getProfileByUserId(1L)
        );

        verify(profileRepository).findByUserId(1L);
    }

    // --------------------------------------------------
    // updateProfile()
    // --------------------------------------------------

    @Test
    void updateProfile_ShouldUpdateFields_WhenProfileExists() {

        ServiceAdvisorUpdateRequest request =
                ServiceAdvisorUpdateRequest.builder()
                        .specialization("Transmission")
                        .experience(7)
                        .available(false)
                        .build();

        when(profileRepository.findByUserId(1L))
                .thenReturn(Optional.of(profile));

        when(profileRepository.save(any(ServiceAdvisorProfile.class)))
                .thenReturn(profile);

        ServiceAdvisorResponse response =
                serviceAdvisorService.updateProfile(1L, request);

        assertEquals("Transmission", response.getSpecialization());
        assertEquals(7, response.getExperience());
        assertFalse(response.getAvailable());

        verify(profileRepository).findByUserId(1L);
        verify(profileRepository).save(profile);
    }

    @Test
    void updateProfile_ShouldKeepExistingValues_WhenUpdateFieldsAreNull() {

        ServiceAdvisorUpdateRequest request =
                ServiceAdvisorUpdateRequest.builder()
                        .specialization(null)
                        .experience(null)
                        .available(null)
                        .build();

        when(profileRepository.findByUserId(1L))
                .thenReturn(Optional.of(profile));

        when(profileRepository.save(any(ServiceAdvisorProfile.class)))
                .thenReturn(profile);

        ServiceAdvisorResponse response =
                serviceAdvisorService.updateProfile(1L, request);

        assertEquals("Engine", response.getSpecialization());
        assertEquals(5, response.getExperience());
        assertTrue(response.getAvailable());

        verify(profileRepository).findByUserId(1L);
        verify(profileRepository).save(profile);
    }

    @Test
    void updateProfile_ShouldThrowException_WhenProfileDoesNotExist() {

        ServiceAdvisorUpdateRequest request =
                ServiceAdvisorUpdateRequest.builder()
                        .specialization("Transmission")
                        .experience(7)
                        .available(false)
                        .build();

        when(profileRepository.findByUserId(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ServiceAdvisorProfileNotFoundException.class,
                () -> serviceAdvisorService.updateProfile(1L, request)
        );

        verify(profileRepository).findByUserId(1L);

        verify(profileRepository, never())
                .save(any(ServiceAdvisorProfile.class));
    }
}
