package com.vehicare.modules.user.api;

import org.springframework.stereotype.Service;

import com.vehicare.modules.auth.dto.UserAuthData;
import com.vehicare.modules.user.entity.User;
import com.vehicare.modules.user.exception.UserNotFoundException;
import com.vehicare.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserAPIImpl implements UserAPI{

	private final UserRepository userRepository;

	@Override
	public UserAuthData findByEmail(String email) {


		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserNotFoundException( "User not found with id: " +email ));

		return mapToUserAuthData(user);
	}


	private UserAuthData mapToUserAuthData(User user) {

		if (user == null) {
			return null;
		}

		return UserAuthData.builder().id(user.getId()).email(user.getEmail()).password(user.getPassword()).role(user.getRole()).enabled(true) .build();
	}

}
