package com.vehicare.modules.email.service;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vehicare.modules.email.api.EmailService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class OwnerEmailServiceImplTest {

	@Mock
	private EmailService emailService;

	@InjectMocks
	private OwnerEmailServiceImpl ownerEmailService;

	@Test
	void sendWelcomeEmail_shouldSendEmailSuccessfully() {

		ownerEmailService.sendWelcomeEmail("owner@vehicare.com", "Chetan");

		verifyEmail("owner@vehicare.com", "Welcome to Vehicare", """
				Hello Chetan,

				Welcome to Vehicare.

				Your owner account has been successfully created.

				Regards,
				Vehicare Team
				""");
	}

	@Test
	void sendVehicleRegistrationEmail_shouldSendEmailSuccessfully() {

		ownerEmailService.sendVehicleRegistrationEmail("owner@vehicare.com", "Chetan", "MH12AB1234");

		verifyEmail("owner@vehicare.com", "Vehicle Registered Successfully", """
				Hello Chetan,

				Your vehicle has been successfully registered.

				Vehicle Number: MH12AB1234

				Regards,
				Vehicare Team
				""");
	}

	@Test
	void sendAppointmentConfirmationEmail_shouldSendEmailSuccessfully() {

		ownerEmailService.sendAppointmentConfirmationEmail("owner@vehicare.com", "Chetan", "25 September 2026",
				"10:00 AM");

		verifyEmail("owner@vehicare.com", "Service Appointment Confirmed", """
				Hello Chetan,

				Your vehicle service appointment has been confirmed.

				Date: 25 September 2026
				Time: 10:00 AM

				Regards,
				Vehicare Team
				""");
	}

	@Test
	void sendAppointmentCancellationEmail_shouldSendEmailSuccessfully() {

		ownerEmailService.sendAppointmentCancellationEmail("owner@vehicare.com", "Chetan", "25 September 2026",
				"10:00 AM");

		verifyEmail("owner@vehicare.com", "Service Appointment Cancelled", """
				Hello Chetan,

				Your service appointment has been cancelled.

				Date: 25 September 2026
				Time: 10:00 AM

				Regards,
				Vehicare Team
				""");
	}

	@Test
	void sendAppointmentRescheduledEmail_shouldSendEmailSuccessfully() {

		ownerEmailService.sendAppointmentRescheduledEmail("owner@vehicare.com", "Chetan", "27 September 2026",
				"02:00 PM");

		verifyEmail("owner@vehicare.com", "Service Appointment Rescheduled", """
				Hello Chetan,

				Your service appointment has been rescheduled.

				New Date: 27 September 2026
				New Time: 02:00 PM

				Regards,
				Vehicare Team
				""");
	}

	@Test
	void sendAppointmentReminderEmail_shouldSendEmailSuccessfully() {

		ownerEmailService.sendAppointmentReminderEmail("owner@vehicare.com", "Chetan", "28 September 2026", "11:00 AM");

		verifyEmail("owner@vehicare.com", "Service Appointment Reminder", """
				Hello Chetan,

				This is a reminder about your upcoming service appointment.

				Date: 28 September 2026
				Time: 11:00 AM

				Regards,
				Vehicare Team
				""");
	}

	@Test
	void sendServiceCompletedEmail_shouldSendEmailSuccessfully() {

		ownerEmailService.sendServiceCompletedEmail("owner@vehicare.com", "Chetan", "MH12AB1234");

		verifyEmail("owner@vehicare.com", "Vehicle Service Completed", """
				Hello Chetan,

				Your vehicle service has been completed.

				Vehicle Number: MH12AB1234

				Regards,
				Vehicare Team
				""");
	}

	@Test
	void sendPaymentConfirmationEmail_shouldSendEmailSuccessfully() {

		ownerEmailService.sendPaymentConfirmationEmail("owner@vehicare.com", "Chetan", "INV-1001", "₹2500");

		verifyEmail("owner@vehicare.com", "Payment Confirmation", """
				Hello Chetan,

				Your payment has been successfully received.

				Invoice Number: INV-1001
				Amount: ₹2500

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