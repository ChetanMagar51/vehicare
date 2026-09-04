package com.vehicare.modules.user.api;

import com.vehicare.modules.auth.dto.UserAuthData;

public interface UserAPI {
	
	UserAuthData findByEmail(String email);

	 
}
