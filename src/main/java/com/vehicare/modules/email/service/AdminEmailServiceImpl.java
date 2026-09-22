package com.vehicare.modules.email.service;

import org.springframework.stereotype.Service;

import com.vehicare.modules.email.api.AdminEmailService;
import com.vehicare.modules.email.api.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminEmailServiceImpl implements AdminEmailService {

    private final EmailService emailService;

    @Override
    public void sendOwnerCreatedEmail(
            String adminEmail,
            String ownerName) {

        String subject = "New Owner Registered";

        String body = """
                Hello Admin,

                A new owner has been registered in Vehicare.

                Owner Name: %s

                Regards,
                Vehicare System
                """.formatted(ownerName);

        emailService.sendEmail(adminEmail, subject, body);
    }

    @Override
    public void sendServiceAdvisorCreatedEmail(
            String adminEmail,
            String advisorName) {

        String subject = "New Service Advisor Registered";

        String body = """
                Hello Admin,

                A new service advisor has been registered.

                Service Advisor: %s

                Regards,
                Vehicare System
                """.formatted(advisorName);

        emailService.sendEmail(adminEmail, subject, body);
    }

    @Override
    public void sendSystemNotification(
            String adminEmail,
            String subject,
            String message) {

        emailService.sendEmail(
                adminEmail,
                subject,
                message
        );
    }
}