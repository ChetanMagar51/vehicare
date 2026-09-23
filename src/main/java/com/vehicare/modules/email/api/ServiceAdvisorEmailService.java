package com.vehicare.modules.email.api;

public interface ServiceAdvisorEmailService {

	void sendAppointmentAssignedEmail(String advisorEmail, String advisorName, String appointmentDate,
			String appointmentTime);

	void sendAppointmentCancelledEmail(String advisorEmail, String advisorName, String appointmentDate,
			String appointmentTime);

	void sendAppointmentRescheduledEmail(String advisorEmail, String advisorName, String appointmentDate,
			String appointmentTime);
}