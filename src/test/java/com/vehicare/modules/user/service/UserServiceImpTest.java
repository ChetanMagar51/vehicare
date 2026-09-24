
package com.vehicare.modules.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vehicare.modules.email.api.UserEmailService;
import com.vehicare.modules.user.dto.RegisterRequest;
import com.vehicare.modules.user.dto.UpdateUserRequest;
import com.vehicare.modules.user.dto.UserDto;
import com.vehicare.modules.user.entity.Role;
import com.vehicare.modules.user.entity.User;
import com.vehicare.modules.user.exception.EmailAlreadyExistsException;
import com.vehicare.modules.user.exception.UserNotFoundException;
import com.vehicare.modules.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImpTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private UserEmailService userEmailService;

	@InjectMocks
	private UserServiceImp userService;

	private RegisterRequest registerRequest;
	private User user;
	private User savedUser;

	@BeforeEach
	void setUp() {

		registerRequest = RegisterRequest.builder().firstName("Chetan").lastName("Magar").phone("9876543210")
				.address("Pune").email("chetan@gmail.com").password("password123").build();

		user = User.builder().id(1L).firstName("Chetan").lastName("Magar").phone("9876543210").address("Pune")
				.email("chetan@gmail.com").password("encodedPassword").role(Role.Owner).build();

		savedUser = User.builder().id(1L).firstName("Chetan").lastName("Magar").phone("9876543210").address("Pune")
				.email("chetan@gmail.com").password("encodedPassword").role(Role.Owner).build();
	}

	// =========================================================
	// createUser(RegisterRequest request)
	// =========================================================

	@Test
	void createUser_ShouldCreateUserSuccessfully() {

		when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);

		when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");

		when(userRepository.save(any(User.class))).thenReturn(savedUser);

		UserDto result = userService.createUser(registerRequest);

		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals("Chetan", result.getFirstName());
		assertEquals("Magar", result.getLastName());
		assertEquals("chetan@gmail.com", result.getEmail());
		assertEquals("9876543210", result.getPhone());
		assertEquals("Pune", result.getAddress());
		assertEquals(Role.Owner, result.getRole());

		verify(userRepository, times(1)).existsByEmail(registerRequest.getEmail());

		verify(passwordEncoder, times(1)).encode(registerRequest.getPassword());

		verify(userRepository, times(1)).save(any(User.class));

		verify(userEmailService).sendWelcomeEmail(result.getEmail(), result.getFirstName(), Role.Owner);
	}

	// =========================================================
	// createUser(RegisterRequest request, Role role)
	// =========================================================

	@Test
	void createUserWithRole_ShouldCreateUserSuccessfully() {

		Role role = Role.Service_Adviser;

		when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);

		when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");

		User serviceAdvisor = User.builder().id(2L).firstName("Chetan").lastName("Magar").phone("9876543210")
				.address("Pune").email("chetan@gmail.com").password("encodedPassword").role(role).build();

		when(userRepository.save(any(User.class))).thenReturn(serviceAdvisor);

		UserDto result = userService.createUser(registerRequest, role);

		assertNotNull(result);
		assertEquals(2L, result.getId());
		assertEquals("Chetan", result.getFirstName());
		assertEquals("chetan@gmail.com", result.getEmail());

		verify(userRepository, times(1)).existsByEmail(registerRequest.getEmail());

		verify(passwordEncoder, times(1)).encode(registerRequest.getPassword());

		verify(userRepository, times(1)).save(any(User.class));

		verify(userEmailService).sendWelcomeEmail(serviceAdvisor.getEmail(), serviceAdvisor.getFirstName(),
				serviceAdvisor.getRole());

	}

	@Test
	void createUserWithRole_ShouldThrowException_WhenRoleIsNull() {

		assertThrows(IllegalArgumentException.class, () -> userService.createUser(registerRequest, null));

		verifyNoInteractions(userRepository);
		verifyNoInteractions(userEmailService);
	}

	@Test
	void createUserWithRole_ShouldThrowException_WhenEmailAlreadyExists() {

		Role role = Role.Service_Adviser;

		when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

		assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(registerRequest, role));

		verify(userRepository, times(1)).existsByEmail(registerRequest.getEmail());

		verify(userRepository, never()).save(any(User.class));

		verify(passwordEncoder, never()).encode(any(String.class));

		verifyNoInteractions(userEmailService);
	}

	// =========================================================
	// getUserById(Long id)
	// =========================================================

	@Test
	void getUserById_ShouldReturnUser_WhenUserExists() {

		Long id = 1L;

		when(userRepository.findById(id)).thenReturn(Optional.of(user));

		UserDto result = userService.getUserById(id);

		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals("Chetan", result.getFirstName());
		assertEquals("Magar", result.getLastName());
		assertEquals("chetan@gmail.com", result.getEmail());

		verify(userRepository, times(1)).findById(id);
	}

	@Test
	void getUserById_ShouldThrowException_WhenUserDoesNotExist() {

		Long id = 100L;

		when(userRepository.findById(id)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> userService.getUserById(id));

		verify(userRepository, times(1)).findById(id);
	}

	// =========================================================
	// getAllUsers()
	// =========================================================

	@Test
	void getAllUsers_ShouldReturnAllUsers() {

		User user2 = User.builder().id(2L).firstName("Rahul").lastName("Patil").phone("9999999999").address("Pune")
				.email("rahul@gmail.com").password("encoded").role(Role.Owner).build();

		when(userRepository.findAll()).thenReturn(Arrays.asList(user, user2));

		List<UserDto> result = userService.getAllUsers();

		assertNotNull(result);
		assertEquals(2, result.size());

		assertEquals("Chetan", result.get(0).getFirstName());
		assertEquals("Rahul", result.get(1).getFirstName());

		verify(userRepository, times(1)).findAll();
	}

	@Test
	void getAllUsers_ShouldReturnEmptyList_WhenNoUsersExist() {

		when(userRepository.findAll()).thenReturn(Collections.emptyList());

		List<UserDto> result = userService.getAllUsers();

		assertNotNull(result);
		assertEquals(0, result.size());

		verify(userRepository, times(1)).findAll();
	}

	// =========================================================
	// getUsersByRole(Role role)
	// =========================================================

	@Test
	void getUsersByRole_ShouldReturnUsersWithGivenRole() {

		Role role = Role.Owner;

		User owner2 = User.builder().id(2L).firstName("Rahul").lastName("Patil").phone("9999999999").address("Pune")
				.email("rahul@gmail.com").password("encoded").role(Role.Owner).build();

		when(userRepository.findAllByRole(role)).thenReturn(Arrays.asList(user, owner2));

		List<UserDto> result = userService.getUsersByRole(role);

		assertNotNull(result);
		assertEquals(2, result.size());

		assertEquals("Chetan", result.get(0).getFirstName());
		assertEquals("Rahul", result.get(1).getFirstName());

		verify(userRepository, times(1)).findAllByRole(role);
	}

	@Test
	void getUsersByRole_ShouldReturnEmptyList_WhenNoUsersWithRoleExist() {

		Role role = Role.Service_Adviser;

		when(userRepository.findAllByRole(role)).thenReturn(Collections.emptyList());

		List<UserDto> result = userService.getUsersByRole(role);

		assertNotNull(result);
		assertEquals(0, result.size());

		verify(userRepository, times(1)).findAllByRole(role);
	}

	// =========================================================
	// getUsersByEmail()
	// =========================================================

	@Test
	void getUserByEmail_ShouldReturnUser_WhenUserExists() {

		String email = "chetan@gmail.com";

		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

		UserDto result = userService.getUserByEmail(email);

		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals("Chetan", result.getFirstName());
		assertEquals("Magar", result.getLastName());
		assertEquals("chetan@gmail.com", result.getEmail());
		assertEquals(Role.Owner, result.getRole());

		verify(userRepository, times(1)).findByEmail(email);

	}

	@Test
	void getUserByEmail_ShouldThrowException_WhenUserDoesNotExist() {

		String email = "unknown@gmail.com";

		when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail(email));

		verify(userRepository, times(1)).findByEmail(email);
	}

	// =========================================================
	// deleteUser(Long id)
	// =========================================================

	@Test
	void deleteUser_ShouldDeleteUser_WhenUserExists() {

		Long id = 1L;

		when(userRepository.findById(id)).thenReturn(Optional.of(user));

		userService.deleteUser(id);

		verify(userRepository, times(1)).findById(id);

		verify(userRepository, times(1)).delete(user);
	}

	@Test
	void deleteUser_ShouldThrowException_WhenUserDoesNotExist() {

		Long id = 100L;

		when(userRepository.findById(id)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> userService.deleteUser(id));

		verify(userRepository, times(1)).findById(id);

		verify(userRepository, never()).delete(any(User.class));
	}

	// =========================================================
	// enableUser(Long id)
	// =========================================================

	@Test
	void enableUser_ShouldEnableUserAndSendEmail_WhenUserExists() {

		Long id = 1L;

		when(userRepository.findById(id)).thenReturn(Optional.of(user));

		user.setEnabled(false);

		userService.enableUser(id);

		assertEquals(true, user.isEnabled());

		verify(userRepository, times(1)).findById(id);

		verify(userEmailService, times(1)).sendAccountEnabledEmail(user.getEmail(), user.getFirstName());
	}

	@Test
	void enableUser_ShouldThrowException_WhenUserDoesNotExist() {

		Long id = 100L;

		when(userRepository.findById(id)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> userService.enableUser(id));

		verify(userRepository, times(1)).findById(id);

		verifyNoInteractions(userEmailService);
	}

	// =========================================================
	// disableUser(Long id)
	// =========================================================

	@Test
	void disableUser_shouldDisableandSendEmail_whenUserExists() {
		Long id = 1L;

		when(userRepository.findById(id)).thenReturn(Optional.of(user));

		user.setEnabled(true);

		userService.disableUser(id);

		assertEquals(false, user.isEnabled());

		verify(userRepository, times(1)).findById(id);

		verify(userEmailService, times(1)).sendAccountDisabledEmail(user.getEmail(), user.getFirstName());

	}

	@Test
	void disableUser_shouldThrowException_whenUserNotExists() {
		Long id = 2l;

		when(userRepository.findById(id)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> userService.disableUser(id));

		verify(userRepository, times(1)).findById(id);

		verifyNoInteractions(userEmailService);

	}

	// =========================================================
	// disableUser(Long id)
	// =========================================================

	@Test
	void updateUser_ShouldUpdateUserSuccessfully() {

		Long id = 1L;

		UpdateUserRequest request = UpdateUserRequest.builder().firstName("Chetan Updated").lastName("Magar Updated")
				.phone("9999999999").address("Mumbai").build();

		when(userRepository.findById(id)).thenReturn(Optional.of(user));

		when(userRepository.save(any(User.class))).thenReturn(user);

		UserDto result = userService.updateUser(id, request);

		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals("Chetan Updated", result.getFirstName());
		assertEquals("Magar Updated", result.getLastName());
		assertEquals("9999999999", result.getPhone());
		assertEquals("Mumbai", result.getAddress());

		// These should remain unchanged
		assertEquals("chetan@gmail.com", result.getEmail());
		assertEquals(Role.Owner, result.getRole());

		verify(userRepository, times(1)).findById(id);
		verify(userRepository, times(1)).save(any(User.class));

		
	}

	@Test
	void updateUser_ShouldThrowException_WhenUserDoesNotExist() {

		Long id = 100L;

		UpdateUserRequest request = UpdateUserRequest.builder().firstName("Chetan").lastName("Magar")
				.phone("9999999999").address("Mumbai").build();

		when(userRepository.findById(id)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> userService.updateUser(id, request));

		verify(userRepository, times(1)).findById(id);

		verify(userRepository, never()).save(any(User.class));

		
	}

}