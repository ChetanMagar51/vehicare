package com.vehicare.modules.email.service;

import org.springframework.stereotype.Service;

import com.vehicare.modules.email.api.EmailService;
import com.vehicare.modules.email.api.UserEmailService;
import com.vehicare.modules.user.entity.Role;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserEmailServiceImpl implements UserEmailService {

	private final EmailService emailService;

	@Override
	public void sendWelcomeEmail(String userEmail, String userName, Role role) {

		String subject = "Welcome to Vehicare";

		String roleMessage;

		switch (role) {
		case Owner:
			roleMessage = getOwnerWelcomeMessage();
			break;

		case Service_Adviser:
			roleMessage = getServiceAdvisorWelcomeMessage();
			break;

		case Sub_Admin:
			roleMessage = getSubAdminWelcomeMessage();
			break;

		case Admin:
			roleMessage = getAdminWelcomeMessage();
			break;

		default:
			roleMessage = getDefaultWelcomeMessage();
		}

		String body = """
				Hello %s,

				Welcome to Vehicare!

				%s

				Regards,
				Vehicare Team
				""".formatted(userName, roleMessage);

		emailService.sendEmail(userEmail, subject, body);
	}

	@Override
	public void sendPasswordChangedEmail(String userEmail, String userName) {

		String subject = "Password Changed Successfully";

		String body = """
				Hello %s,

				Your Vehicare account password has been changed successfully.

				If you did not make this change, please contact the Vehicare support team immediately.

				Regards,
				Vehicare Team
				""".formatted(userName);

		emailService.sendEmail(userEmail, subject, body);
	}

	@Override
	public void sendAccountEnabledEmail(String userEmail, String userName) {

		String subject = "Vehicare Account Enabled";

		String body = """
				Hello %s,

				Your Vehicare account has been enabled.

				You can now log in and access your account.

				Regards,
				Vehicare Team
				""".formatted(userName);

		emailService.sendEmail(userEmail, subject, body);
	}

	@Override
	public void sendAccountDisabledEmail(String userEmail, String userName) {

		String subject = "Vehicare Account Disabled";

		String body = """
				Hello %s,

				Your Vehicare account has been disabled.

				You will not be able to log in while your account is disabled.

				Please contact the Vehicare administrator if you believe this was done incorrectly.

				Regards,
				Vehicare Team
				""".formatted(userName);

		emailService.sendEmail(userEmail, subject, body);
	}

	private String getOwnerWelcomeMessage() {
		return """
				Your Owner account has been created successfully.

				You can now register your vehicles, schedule services,
				view service details, and manage your appointments.
				""";
	}

	private String getServiceAdvisorWelcomeMessage() {
		return """
				Your Service Advisor account has been created successfully.

				You can now manage service appointments,
				assist vehicle owners, and handle assigned service activities.
				""";
	}

	private String getSubAdminWelcomeMessage() {
		return """
				Your Sub Admin account has been created successfully.

				You can now access the administrative features
				assigned to your role.
				""";
	}

	private String getAdminWelcomeMessage() {
		return """
				Your Admin account has been created successfully.

				You can now access the Vehicare administration features
				available to you.
				""";
	}

	private String getDefaultWelcomeMessage() {
		return """
				Your account has been created successfully.

				You can now access the Vehicare services available to you.
				""";
	}
}