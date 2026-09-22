package com.vehicare.modules.email.api;

public interface EmailService {

	void sendEmail(String to, String subject, String body);

	}