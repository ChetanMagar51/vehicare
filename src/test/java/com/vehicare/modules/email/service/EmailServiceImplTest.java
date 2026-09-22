package com.vehicare.modules.email.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.util.ReflectionTestUtils;

import com.vehicare.modules.email.exception.EmailSendingException;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

	@Mock
	private org.springframework.mail.javamail.JavaMailSender mailSender;

	@InjectMocks
	private EmailServiceImpl emailService;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(emailService, "senderEmail", "vehicare@example.com");
	}

	@Test
	void sendEmail_shouldSendEmailSuccessfully() {

		emailService.sendEmail("owner@example.com", "Service Reminder", "Your vehicle service is due.");

		ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

		verify(mailSender).send(captor.capture());

		SimpleMailMessage message = captor.getValue();

		assertEquals("vehicare@example.com", message.getFrom());
		assertEquals("owner@example.com", message.getTo()[0]);
		assertEquals("Service Reminder", message.getSubject());
		assertEquals("Your vehicle service is due.", message.getText());
	}

	@Test
	void sendEmail_shouldThrowException_whenRecipientIsNull() {

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> emailService.sendEmail(null, "Service Reminder", "Your vehicle service is due."));

		assertEquals("Recipient email is required", exception.getMessage());

		verify(mailSender, never()).send(any(SimpleMailMessage.class));
	}

	@Test
	void sendEmail_shouldThrowException_whenRecipientIsBlank() {

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> emailService.sendEmail("   ", "Service Reminder", "Your vehicle service is due."));

		assertEquals("Recipient email is required", exception.getMessage());

		verify(mailSender, never()).send(any(SimpleMailMessage.class));
	}

	@Test
	void sendEmail_shouldThrowException_whenSubjectIsNull() {

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> emailService.sendEmail("owner@example.com", null, "Your vehicle service is due."));

		assertEquals("Email subject is required", exception.getMessage());

		verify(mailSender, never()).send(any(SimpleMailMessage.class));
	}

	@Test
	void sendEmail_shouldThrowException_whenSubjectIsBlank() {

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> emailService.sendEmail("owner@example.com", "   ", "Your vehicle service is due."));

		assertEquals("Email subject is required", exception.getMessage());

		verify(mailSender, never()).send(any(SimpleMailMessage.class));
	}

	@Test
	void sendEmail_shouldThrowException_whenBodyIsNull() {

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> emailService.sendEmail("owner@example.com", "Service Reminder", null));

		assertEquals("Email body is required", exception.getMessage());

		verify(mailSender, never()).send(any(SimpleMailMessage.class));
	}

	@Test
	void sendEmail_shouldThrowException_whenBodyIsBlank() {

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> emailService.sendEmail("owner@example.com", "Service Reminder", "   "));

		assertEquals("Email body is required", exception.getMessage());

		verify(mailSender, never()).send(any(SimpleMailMessage.class));
	}

	@Test
	void sendEmail_shouldThrowEmailSendingException_whenMailSenderFails() {

	    MailException mailException =
	            new MailException("SMTP server unavailable") {
	            };

	    doThrow(mailException)
	            .when(mailSender)
	            .send(any(SimpleMailMessage.class));

	    EmailSendingException exception = assertThrows(
	            EmailSendingException.class,
	            () -> emailService.sendEmail(
	                    "owner@example.com",
	                    "Service Reminder",
	                    "Your vehicle service is due."
	            )
	    );

	    assertEquals("Failed to send email", exception.getMessage());

	    verify(mailSender).send(any(SimpleMailMessage.class));
	}
}