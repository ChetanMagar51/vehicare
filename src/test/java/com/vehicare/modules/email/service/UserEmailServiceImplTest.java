package com.vehicare.modules.email.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vehicare.modules.email.api.EmailService;
import com.vehicare.modules.user.entity.Role;

@ExtendWith(MockitoExtension.class)
class UserEmailServiceImplTest {

	@Mock
	private EmailService emailService;

	@InjectMocks
	private UserEmailServiceImpl userEmailService;

	@Test
	void sendWelcomeEmail_shouldSendOwnerWelcomeEmail() {

		userEmailService.sendWelcomeEmail("owner@vehicare.com", "Chetan", Role.Owner);

		verifyEmail("owner@vehicare.com", "Welcome to Vehicare", """
				Hello Chetan,

				Welcome to Vehicare!

				Your Owner account has been created successfully.

				You can now register your vehicles, schedule services,
				view service details, and manage your appointments.


				Regards,
				Vehicare Team
				""");
	}

	@Test
	void sendWelcomeEmail_shouldSendServiceAdvisorWelcomeEmail() {

		userEmailService.sendWelcomeEmail("advisor@vehicare.com", "Rahul", Role.Service_Adviser);

		verifyEmail("advisor@vehicare.com", "Welcome to Vehicare", """
				Hello Rahul,

				Welcome to Vehicare!

				Your Service Advisor account has been created successfully.

				You can now manage service appointments,
				assist vehicle owners, and handle assigned service activities.


				Regards,
				Vehicare Team
				""");
	}

	@Test
	void sendWelcomeEmail_shouldSendSubAdminWelcomeEmail() {

		userEmailService.sendWelcomeEmail("subadmin@vehicare.com", "Amit", Role.Sub_Admin);

		verifyEmail("subadmin@vehicare.com", "Welcome to Vehicare", """
				Hello Amit,

				Welcome to Vehicare!

				Your Sub Admin account has been created successfully.

				You can now access the administrative features
				assigned to your role.

 
				Regards,
				Vehicare Team
				""");
	}

	@Test
	void sendWelcomeEmail_shouldSendAdminWelcomeEmail() {

		userEmailService.sendWelcomeEmail("admin@vehicare.com", "Admin", Role.Admin);

		verifyEmail("admin@vehicare.com", "Welcome to Vehicare", """
				Hello Admin,

				Welcome to Vehicare!

				Your Admin account has been created successfully.

				You can now access the Vehicare administration features
				available to you.


				Regards,
				Vehicare Team
				""");
	}

	@Test
	void sendPasswordChangedEmail_shouldSendEmailSuccessfully() {

		userEmailService.sendPasswordChangedEmail("user@vehicare.com", "Chetan");

		verifyEmail("user@vehicare.com", "Password Changed Successfully", """
				Hello Chetan,

				Your Vehicare account password has been changed successfully.

				If you did not make this change, please contact the Vehicare support team immediately.

				Regards,
				Vehicare Team
				""");
	}

	@Test
	void sendAccountEnabledEmail_shouldSendEmailSuccessfully() {

		userEmailService.sendAccountEnabledEmail("user@vehicare.com", "Chetan");

		verifyEmail("user@vehicare.com", "Vehicare Account Enabled", """
				Hello Chetan, 

				Your Vehicare account has been enabled.

				You can now log in and access your account.

				Regards,
				Vehicare Team
				""");
	}

	@Test
	void sendAccountDisabledEmail_shouldSendEmailSuccessfully() {

		userEmailService.sendAccountDisabledEmail("user@vehicare.com", "Chetan");

		verifyEmail("user@vehicare.com", "Vehicare Account Disabled", """
				Hello Chetan,

				Your Vehicare account has been disabled.

				You will not be able to log in while your account is disabled.

				Please contact the Vehicare administrator if you believe this was done incorrectly.

				Regards,
				Vehicare Team
				""");
	}

	private void verifyEmail(String expectedEmail, String expectedSubject, String expectedBody) {

		ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);

		ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);

		ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

		verify(emailService).sendEmail(emailCaptor.capture(), subjectCaptor.capture(), bodyCaptor.capture());

		assertEquals(expectedEmail, emailCaptor.getValue());
		assertEquals(expectedSubject, subjectCaptor.getValue());
		assertEquals(expectedBody, bodyCaptor.getValue());
	}
}