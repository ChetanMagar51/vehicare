package com.vehicare.modules.email.api;

public interface AdminEmailService {

	void sendOwnerCreatedEmail(String adminEmail, String ownerName);

	void sendServiceAdvisorCreatedEmail(String adminEmail, String advisorName);

	void sendSystemNotification(String adminEmail, String subject, String message);
}