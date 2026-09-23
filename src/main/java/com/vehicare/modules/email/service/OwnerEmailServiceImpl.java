package com.vehicare.modules.email.service;

import org.springframework.stereotype.Service;

import com.vehicare.modules.email.api.EmailService;
import com.vehicare.modules.email.api.OwnerEmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OwnerEmailServiceImpl implements OwnerEmailService {

	private final EmailService emailService;

	
	@Override
	public void sendVehicleRegistrationEmail(String ownerEmail, String ownerName, String vehicleNumber) {

		String subject = "Vehicle Registered Successfully";

		String body = """
				Hello %s,

				Your vehicle has been successfully registered.

				Vehicle Number: %s

				Regards,
				Vehicare Team
				""".formatted(ownerName, vehicleNumber);

		emailService.sendEmail(ownerEmail, subject, body);
	}

	@Override
	public void sendAppointmentConfirmationEmail(String ownerEmail, String ownerName, String appointmentDate,
			String appointmentTime) {

		String subject = "Service Appointment Confirmed";

		String body = """
				Hello %s,

				Your vehicle service appointment has been confirmed.

				Date: %s
				Time: %s

				Regards,
				Vehicare Team
				""".formatted(ownerName, appointmentDate, appointmentTime);

		emailService.sendEmail(ownerEmail, subject, body);
	}

	@Override
	public void sendAppointmentCancellationEmail(String ownerEmail, String ownerName, String appointmentDate,
			String appointmentTime) {

		String subject = "Service Appointment Cancelled";

		String body = """
				Hello %s,

				Your service appointment has been cancelled.

				Date: %s
				Time: %s

				Regards,
				Vehicare Team
				""".formatted(ownerName, appointmentDate, appointmentTime);

		emailService.sendEmail(ownerEmail, subject, body);
	}

	@Override
	public void sendAppointmentRescheduledEmail(String ownerEmail, String ownerName, String appointmentDate,
			String appointmentTime) {

		String subject = "Service Appointment Rescheduled";

		String body = """
				Hello %s,

				Your service appointment has been rescheduled.

				New Date: %s
				New Time: %s

				Regards,
				Vehicare Team
				""".formatted(ownerName, appointmentDate, appointmentTime);

		emailService.sendEmail(ownerEmail, subject, body);
	}

	@Override
	public void sendAppointmentReminderEmail(String ownerEmail, String ownerName, String appointmentDate,
			String appointmentTime) {

		String subject = "Service Appointment Reminder";

		String body = """
				Hello %s,

				This is a reminder about your upcoming service appointment.

				Date: %s
				Time: %s

				Regards,
				Vehicare Team
				""".formatted(ownerName, appointmentDate, appointmentTime);

		emailService.sendEmail(ownerEmail, subject, body);
	}

	@Override
	public void sendServiceCompletedEmail(String ownerEmail, String ownerName, String vehicleNumber) {

		String subject = "Vehicle Service Completed";

		String body = """
				Hello %s,

				Your vehicle service has been completed.

				Vehicle Number: %s

				Regards,
				Vehicare Team
				""".formatted(ownerName, vehicleNumber);

		emailService.sendEmail(ownerEmail, subject, body);
	}

	@Override
	public void sendPaymentConfirmationEmail(String ownerEmail, String ownerName, String invoiceNumber, String amount) {

		String subject = "Payment Confirmation";

		String body = """
				Hello %s,

				Your payment has been successfully received.

				Invoice Number: %s
				Amount: %s

				Regards,
				Vehicare Team
				""".formatted(ownerName, invoiceNumber, amount);

		emailService.sendEmail(ownerEmail, subject, body);
	}
}