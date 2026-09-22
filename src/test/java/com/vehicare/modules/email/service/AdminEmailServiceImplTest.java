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

@ExtendWith(MockitoExtension.class)
class AdminEmailServiceImplTest {

	@Mock
	private EmailService emailService;

	@InjectMocks
	private AdminEmailServiceImpl adminEmailService;

	@Test
	void sendOwnerCreatedEmail_shouldSendEmailSuccessfully() {

		adminEmailService.sendOwnerCreatedEmail("admin@vehicare.com", "Chetan");

		ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);

		ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);

		ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

		verify(emailService).sendEmail(emailCaptor.capture(), subjectCaptor.capture(), bodyCaptor.capture());

		assertEquals("admin@vehicare.com", emailCaptor.getValue());

		assertEquals("New Owner Registered", subjectCaptor.getValue());

		assertEquals(
				"""
				Hello Admin,

				A new owner has been registered in Vehicare.

				Owner Name: Chetan

				Regards,
				Vehicare System
				""",
				bodyCaptor.getValue());
	}

	@Test
	void sendServiceAdvisorCreatedEmail_shouldSendEmailSuccessfully() {

		adminEmailService.sendServiceAdvisorCreatedEmail("admin@vehicare.com", "Rahul");

		ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);

		ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);

		ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

		verify(emailService).sendEmail(emailCaptor.capture(), subjectCaptor.capture(), bodyCaptor.capture());

		assertEquals("admin@vehicare.com", emailCaptor.getValue());

		assertEquals("New Service Advisor Registered", subjectCaptor.getValue());

		assertEquals(
				"""
				Hello Admin,

				A new service advisor has been registered.

				Service Advisor: Rahul

				Regards,
				Vehicare System
                """, bodyCaptor.getValue());
	}

	@Test
	void sendSystemNotification_shouldSendEmailSuccessfully() {

		adminEmailService.sendSystemNotification("admin@vehicare.com", "System Notification",
				"A new service request has been created.");

		verify(emailService).sendEmail("admin@vehicare.com", "System Notification",
				"A new service request has been created.");
	}
}