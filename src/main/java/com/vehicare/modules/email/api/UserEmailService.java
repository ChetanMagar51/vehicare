package com.vehicare.modules.email.api;

import com.vehicare.modules.user.entity.Role;

public interface UserEmailService {

	void sendWelcomeEmail(String userEmail, String userName, Role role);

	void sendPasswordChangedEmail(String userEmail, String userName);

	void sendAccountEnabledEmail(String userEmail, String userName);

	void sendAccountDisabledEmail(String userEmail, String userName);
}