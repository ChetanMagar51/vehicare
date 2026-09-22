package com.vehicare.modules.email.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.vehicare.common.api.ApiResponse;

class EmailExceptionHandlerTest {

    private final EmailExceptionHandler exceptionHandler =
            new EmailExceptionHandler();

    @Test
    void handleEmailSendingException_shouldReturnInternalServerError() {

        EmailSendingException exception =
                new EmailSendingException("Failed to send email");

        ResponseEntity<ApiResponse<Void>> response =
                exceptionHandler.handleEmailSendingException(exception);

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );

        assertEquals(
                "ERROR",
                response.getBody().getStatus()
        );

        assertEquals(
                "Failed to send email",
                response.getBody().getMessage()
        );

        assertEquals(
                null,
                response.getBody().getData()
        );
    }
}