package com.vehicare.modules.user.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.vehicare.modules.user.entity.Role;
import com.vehicare.modules.user.entity.User;
import com.vehicare.modules.user.repository.UserRepository;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private User serviceAdvisor;

    @BeforeEach
    void setUp() {

        userRepository.deleteAll();

        owner = User.builder()
                .firstName("Chetan")
                .lastName("Magar")
                .phone("9876543210")
                .address("Pune")
                .email("chetan@gmail.com")
                .password("encodedPassword")
                .role(Role.Owner)
                .build();

        serviceAdvisor = User.builder()
                .firstName("Rahul")
                .lastName("Patil")
                .phone("9999999999")
                .address("Pune")
                .email("rahul@gmail.com")
                .password("encodedPassword")
                .role(Role.Service_Adviser)
                .build();

        userRepository.save(owner);
        userRepository.save(serviceAdvisor);
    }

    // =========================================================
    // findByEmail()
    // =========================================================

    @Test
    void findByEmail_ShouldReturnUser_WhenEmailExists() {

        Optional<User> result =
                userRepository.findByEmail("chetan@gmail.com");

        assertTrue(result.isPresent());

        assertEquals(
                "chetan@gmail.com",
                result.get().getEmail()
        );
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenEmailDoesNotExist() {

        Optional<User> result =
                userRepository.findByEmail("unknown@gmail.com");

        assertTrue(result.isEmpty());
    }


    // =========================================================
    // findByRoleAndId()
    // =========================================================

    @Test
    void findByRoleAndId_ShouldReturnUser_WhenRoleAndIdMatch() {

        Long userId = owner.getId();

        Optional<User> result =
                userRepository.findByRoleAndId(
                        Role.Owner,
                        userId
                );

        assertTrue(result.isPresent());

        assertEquals(
                "chetan@gmail.com",
                result.get().getEmail()
        );
    }

    @Test
    void findByRoleAndId_ShouldReturnEmpty_WhenRoleDoesNotMatch() {

        Long userId = owner.getId();

        Optional<User> result =
                userRepository.findByRoleAndId(
                        Role.Service_Adviser,
                        userId
                );

        assertTrue(result.isEmpty());
    }


    // =========================================================
    // findAllByRole()
    // =========================================================

    @Test
    void findAllByRole_ShouldReturnUsersWithGivenRole() {

        List<User> result =
                userRepository.findAllByRole(Role.Owner);

        assertEquals(1, result.size());

        assertEquals(
                "chetan@gmail.com",
                result.get(0).getEmail()
        );
    }

    @Test
    void findAllByRole_ShouldReturnEmpty_WhenNoUsersWithRoleExist() {

        // Use a role that is not present in the test data.
        // Adjust this according to your actual Role enum.

        List<User> result =
                userRepository.findAllByRole(Role.Admin);

        assertTrue(result.isEmpty());
    }


    // =========================================================
    // existsByEmail()
    // =========================================================

    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {

        boolean result =
                userRepository.existsByEmail("chetan@gmail.com");

        assertTrue(result);
    }

    @Test
    void existsByEmail_ShouldReturnFalse_WhenEmailDoesNotExist() {

        boolean result =
                userRepository.existsByEmail("unknown@gmail.com");

        assertFalse(result);
    }
}