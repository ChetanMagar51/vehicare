package com.vehicare.modules.email.service;

import org.springframework.stereotype.Service;

import com.vehicare.modules.email.api.EmailService;
import com.vehicare.modules.email.api.ServiceAdvisorEmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceAdvisorEmailServiceImpl implements ServiceAdvisorEmailService {

	private final EmailService emailService;

	

	@Override
	public void sendAppointmentAssignedEmail(String advisorEmail, String advisorName, String appointmentDate,
			String appointmentTime) {

		String subject = "New Service Appointment Assigned";

		String body = """
				Hello %s,

				A new service appointment has been assigned to you.

				Date: %s
				Time: %s

				Please check your Vehicare dashboard for details.

				Regards,
				Vehicare Team
				""".formatted(advisorName, appointmentDate, appointmentTime);

		emailService.sendEmail(advisorEmail, subject, body);
	}

	@Override
	public void sendAppointmentCancelledEmail(String advisorEmail, String advisorName, String appointmentDate,
			String appointmentTime) {

		String subject = "Service Appointment Cancelled";

		String body = """
				Hello %s,

				A service appointment assigned to you has been cancelled.

				Date: %s
				Time: %s

				Regards,
				Vehicare Team
				""".formatted(advisorName, appointmentDate, appointmentTime);

		emailService.sendEmail(advisorEmail, subject, body);
	}

	@Override
	public void sendAppointmentRescheduledEmail(String advisorEmail, String advisorName, String appointmentDate,
			String appointmentTime) {

		String subject = "Service Appointment Rescheduled";

		String body = """
				Hello %s,

				A service appointment assigned to you has been rescheduled.

				New Date: %s
				New Time: %s

				Regards,
				Vehicare Team
				""".formatted(advisorName, appointmentDate, appointmentTime);

		emailService.sendEmail(advisorEmail, subject, body);
	}
}