package com.vehicare.modules.user.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vehicare.common.api.ApiResponse;
import com.vehicare.modules.user.dto.RegisterRequest;
import com.vehicare.modules.user.dto.UserDto;
import com.vehicare.modules.user.entity.Role;
import com.vehicare.modules.user.entity.User;
import com.vehicare.modules.user.exception.EmailAlreadyExistsException;
import com.vehicare.modules.user.exception.UserNotFoundException;
import com.vehicare.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService{
	
	private final UserRepository userRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	
	private UserDto mapToUserDto(User user) {

		if (user == null) {
			return null;
		}

		return UserDto.builder().id(user.getId()).firstName(user.getFirstName()).lastName(user.getLastName())
				.email(user.getEmail()).phone(user.getPhone()).address(user.getAddress()).build();
	}

	// Service
	@Override
	@Transactional
	public UserDto createUser(RegisterRequest request) {

	    if (userRepository.existsByEmail(request.getEmail())) {
	        throw new EmailAlreadyExistsException(
	                "User already exists with email: " + request.getEmail()
	        );
	    }

	    User user = User.builder()
	            .firstName(request.getFirstName())
	            .lastName(request.getLastName())
	            .phone(request.getPhone())
	            .address(request.getAddress())
	            .email(request.getEmail())
	            .password(passwordEncoder.encode(request.getPassword()))
	            .role(Role.Owner)
	            .build();

	    User savedUser = userRepository.save(user);

	    return mapToUserDto(savedUser);
	}
	
	@Override
	@Transactional
	public UserDto createUser(RegisterRequest request, Role role) {

	    if (userRepository.existsByEmail(request.getEmail())) {
	        throw new EmailAlreadyExistsException(
	                "User already exists with email: " + request.getEmail()
	        );
	    }

	    User user = User.builder()
	            .firstName(request.getFirstName())
	            .lastName(request.getLastName())
	            .phone(request.getPhone())
	            .address(request.getAddress())
	            .email(request.getEmail())
	            .password(passwordEncoder.encode(request.getPassword()))
	            .role(role)
	            .build();

	    User savedUser = userRepository.save(user);

	    return mapToUserDto(savedUser);
	}
	
	@Override
	public UserDto getUserById(Long id) {
		User user = userRepository.findById(id)
	            .orElseThrow(() -> new UserNotFoundException(
	                    "User not found with id: " + id
	            ));

	    return mapToUserDto(user);
	}

	@Override
	public List<UserDto> getAllUsers() {
		
		
		return userRepository.findAll()
                .stream()
                .map(user->mapToUserDto(user))
                .toList();
	}
	
	@Override
	public List<UserDto> getUsersByRole(Role role) {

	    return userRepository.findAllByRole(role)
	            .stream()
	            .map(this::mapToUserDto)
	            .toList();
	}
	
	@Override
	@Transactional
	public void deleteUser(Long id) {

	    User user = userRepository.findById(id)
	            .orElseThrow(() ->
	                    new UserNotFoundException(
	                            "User not found with id: " + id
	                    ));

	    userRepository.delete(user);
	}
	
	


}
