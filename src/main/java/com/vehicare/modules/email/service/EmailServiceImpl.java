package com.vehicare.modules.email.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.vehicare.modules.email.api.EmailService;
import com.vehicare.modules.email.exception.EmailSendingException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public void sendEmail(String to, String subject, String body) {

        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("Recipient email is required");
        }

        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Email subject is required");
        }

        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("Email body is required");
        }

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        try {
            mailSender.send(message);
        } catch (MailException exception) {
            throw new EmailSendingException(
                    "Failed to send email",
                    exception
            );
        }
    }
}