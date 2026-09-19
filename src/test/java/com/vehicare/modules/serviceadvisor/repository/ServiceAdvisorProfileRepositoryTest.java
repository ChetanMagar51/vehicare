package com.vehicare.modules.serviceadvisor.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.vehicare.modules.serviceadvisor.entity.ServiceAdvisorProfile;

@DataJpaTest
class ServiceAdvisorProfileRepositoryTest {

    @Autowired
    private ServiceAdvisorProfileRepository profileRepository;

    private ServiceAdvisorProfile profile;

    @BeforeEach
    void setUp() {

        profile = ServiceAdvisorProfile.builder()
                .userId(1L)
                .employeeId("SA001")
                .specialization("Engine")
                .experience(5)
                .available(true)
                .build();

        profileRepository.save(profile);
    }

    // --------------------------------------------------
    // findByUserId()
    // --------------------------------------------------

    @Test
    void findByUserId_ShouldReturnProfile_WhenUserIdExists() {

        Optional<ServiceAdvisorProfile> result =
                profileRepository.findByUserId(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getUserId());
        assertEquals("SA001", result.get().getEmployeeId());
    }

    @Test
    void findByUserId_ShouldReturnEmpty_WhenUserIdDoesNotExist() {

        Optional<ServiceAdvisorProfile> result =
                profileRepository.findByUserId(999L);

        assertTrue(result.isEmpty());
    }

    // --------------------------------------------------
    // findByEmployeeId()
    // --------------------------------------------------

    @Test
    void findByEmployeeId_ShouldReturnProfile_WhenEmployeeIdExists() {

        Optional<ServiceAdvisorProfile> result =
                profileRepository.findByEmployeeId("SA001");

        assertTrue(result.isPresent());
        assertEquals("SA001", result.get().getEmployeeId());
        assertEquals(1L, result.get().getUserId());
    }

    @Test
    void findByEmployeeId_ShouldReturnEmpty_WhenEmployeeIdDoesNotExist() {

        Optional<ServiceAdvisorProfile> result =
                profileRepository.findByEmployeeId("SA999");

        assertTrue(result.isEmpty());
    }

    // --------------------------------------------------
    // existsByUserId()
    // --------------------------------------------------

    @Test
    void existsByUserId_ShouldReturnTrue_WhenUserIdExists() {

        boolean result =
                profileRepository.existsByUserId(1L);

        assertTrue(result);
    }

    @Test
    void existsByUserId_ShouldReturnFalse_WhenUserIdDoesNotExist() {

        boolean result =
                profileRepository.existsByUserId(999L);

        assertFalse(result);
    }

    // --------------------------------------------------
    // existsByEmployeeId()
    // --------------------------------------------------

    @Test
    void existsByEmployeeId_ShouldReturnTrue_WhenEmployeeIdExists() {

        boolean result =
                profileRepository.existsByEmployeeId("SA001");

        assertTrue(result);
    }

    @Test
    void existsByEmployeeId_ShouldReturnFalse_WhenEmployeeIdDoesNotExist() {

        boolean result =
                profileRepository.existsByEmployeeId("SA999");

        assertFalse(result);
    }
}

