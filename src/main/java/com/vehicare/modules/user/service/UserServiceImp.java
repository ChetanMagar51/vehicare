package com.vehicare.modules.user.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vehicare.modules.email.api.UserEmailService;
import com.vehicare.modules.user.dto.RegisterRequest;
import com.vehicare.modules.user.dto.UpdateUserRequest;
import com.vehicare.modules.user.dto.UserDto;
import com.vehicare.modules.user.entity.Role;
import com.vehicare.modules.user.entity.User;
import com.vehicare.modules.user.exception.EmailAlreadyExistsException;
import com.vehicare.modules.user.exception.UserNotFoundException;
import com.vehicare.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;
	
	private final UserEmailService userEmailservice;

	private UserDto mapToUserDto(User user) {

		if (user == null) {
			return null;
		}

		return UserDto.builder().id(user.getId()).firstName(user.getFirstName()).lastName(user.getLastName())
				.role(user.getRole()).email(user.getEmail()).phone(user.getPhone()).address(user.getAddress()).build();
	}

	// Service
	@Override
	public UserDto createUser(RegisterRequest request) {

		return createUser(request, Role.Owner);
	}

	@Override
	@Transactional
	public UserDto createUser(RegisterRequest request, Role role) {

		if (userRepository.existsByEmail(request.getEmail())) {
			throw new EmailAlreadyExistsException("User already exists with email: " + request.getEmail());
		}

		User user = User.builder().firstName(request.getFirstName()).lastName(request.getLastName())
				.phone(request.getPhone()).address(request.getAddress()).email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword())).role(role).build();

		User savedUser = userRepository.save(user);
		
		userEmailservice.sendWelcomeEmail(savedUser.getEmail(), savedUser.getUsername(), savedUser.getRole());

		return mapToUserDto(savedUser);
	}

	@Override
	public UserDto getUserById(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

		return mapToUserDto(user);
	}
    
	@Override
	public UserDto getUserByEmail(String email) {
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

		return mapToUserDto(user);
	}
	

	@Override
	public List<UserDto> getAllUsers() {

		return userRepository.findAll().stream().map(user -> mapToUserDto(user)).toList();
	}

	@Override
	public List<UserDto> getUsersByRole(Role role) {

		return userRepository.findAllByRole(role).stream().map(this::mapToUserDto).toList();
	}
	
	@Override
	@Transactional
	public void enableUser(Long id)
	{
		User user = userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
		
		user.setEnabled(true);
		
		userEmailservice.sendAccountEnabledEmail(user.getEmail(), user.getUsername());

		
	}
	
	@Override
	@Transactional
	public void disableUser(Long id)
	{
		User user = userRepository.findById(id)
				.orElseThrow(()-> new UserNotFoundException("User not found with id: " + id));
				
				user.setEnabled(false);
				userEmailservice.sendAccountDisabledEmail(user.getEmail(), user.getUsername());
		
	}
	
	@Override
	@Transactional
	public UserDto updateUser(Long id, UpdateUserRequest request) {

		User user = userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setPhone(request.getPhone());
		user.setAddress(request.getAddress());

		User updatedUser = userRepository.save(user);
		
	

		return mapToUserDto(updatedUser);
	}

	@Override
	@Transactional
	public void deleteUser(Long id) {

		User user = userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

		userRepository.delete(user);
	}

	
	
	

}
