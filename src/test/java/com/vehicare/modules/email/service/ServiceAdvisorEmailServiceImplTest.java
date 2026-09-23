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
class ServiceAdvisorEmailServiceImplTest {

	@Mock
	private EmailService emailService;

	@InjectMocks
	private ServiceAdvisorEmailServiceImpl serviceAdvisorEmailService;

	

	@Test
	void sendAppointmentAssignedEmail_shouldSendEmailSuccessfully() {

		serviceAdvisorEmailService.sendAppointmentAssignedEmail("advisor@vehicare.com", "Rahul", "25 September 2026",
				"10:00 AM");

		verifyEmail("advisor@vehicare.com", "New Service Appointment Assigned", """
				Hello Rahul,

				A new service appointment has been assigned to you.

				Date: 25 September 2026
				Time: 10:00 AM

				Please check your Vehicare dashboard for details.

				Regards,
				Vehicare Team
				""");
	}

	@Test
	void sendAppointmentCancelledEmail_shouldSendEmailSuccessfully() {

		serviceAdvisorEmailService.sendAppointmentCancelledEmail("advisor@vehicare.com", "Rahul", "25 September 2026",
				"10:00 AM");

		verifyEmail("advisor@vehicare.com", "Service Appointment Cancelled", """
				Hello Rahul,

				A service appointment assigned to you has been cancelled.

				Date: 25 September 2026
				Time: 10:00 AM

				Regards,
				Vehicare Team
				""");
	}

	@Test
	void sendAppointmentRescheduledEmail_shouldSendEmailSuccessfully() {

		serviceAdvisorEmailService.sendAppointmentRescheduledEmail("advisor@vehicare.com", "Rahul", "27 September 2026",
				"02:00 PM");

		verifyEmail("advisor@vehicare.com", "Service Appointment Rescheduled", """
				Hello Rahul,

				A service appointment assigned to you has been rescheduled.

				New Date: 27 September 2026
				New Time: 02:00 PM

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