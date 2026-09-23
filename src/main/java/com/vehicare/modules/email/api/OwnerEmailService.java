package com.vehicare.modules.email.api;

public interface OwnerEmailService {


	void sendVehicleRegistrationEmail(String ownerEmail, String ownerName, String vehicleNumber);

	void sendAppointmentConfirmationEmail(String ownerEmail, String ownerName, String appointmentDate,
			String appointmentTime);

	void sendAppointmentCancellationEmail(String ownerEmail, String ownerName, String appointmentDate,
			String appointmentTime);

	void sendAppointmentRescheduledEmail(String ownerEmail, String ownerName, String appointmentDate,
			String appointmentTime);

	void sendAppointmentReminderEmail(String ownerEmail, String ownerName, String appointmentDate,
			String appointmentTime);

	void sendServiceCompletedEmail(String ownerEmail, String ownerName, String vehicleNumber);

	void sendPaymentConfirmationEmail(String ownerEmail, String ownerName, String invoiceNumber, String amount);
}