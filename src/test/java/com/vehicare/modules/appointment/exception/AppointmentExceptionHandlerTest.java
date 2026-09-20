package com.vehicare.modules.appointment.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.vehicare.common.api.ApiResponse;

class AppointmentExceptionHandlerTest {

    private final AppointmentExceptionHandler exceptionHandler =
            new AppointmentExceptionHandler();

    @Test
    void appointmentNotFoundException_shouldReturnConflict() {

        AppointmentNotFoundException exception =
                new AppointmentNotFoundException("Appointment not found");

        ResponseEntity<ApiResponse<Void>> response =
                exceptionHandler.profileAlreadyExistsException(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

        assertEquals("Failed", response.getBody().getStatus());

        assertEquals(
                "Appointment not found",
                response.getBody().getMessage()
        );

        assertEquals(null, response.getBody().getData());
    }
}