package com.vehicare.modules.serviceadvisor.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.vehicare.common.api.ApiResponse;

class AdvisorExceptionHandlerTest {

    private final AdvisorExceptionHandler exceptionHandler =
            new AdvisorExceptionHandler();

    @Test
    void profileAlreadyExistsException_ShouldReturnConflict() {

        ServiceAdvisorProfileAlreadyExistsException exception =
                new ServiceAdvisorProfileAlreadyExistsException(
                        "Service advisor profile already exists"
                );

        ResponseEntity<ApiResponse<Void>> response =
                exceptionHandler.profileAlreadyExistsException(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Failed", response.getBody().getStatus());
        assertEquals(
                "Service advisor profile already exists",
                response.getBody().getMessage()
        );
        assertNull(response.getBody().getData());
    }

    @Test
    void handleUserNotFoundException_ShouldReturnNotFound() {

        ServiceAdvisorProfileNotFoundException exception =
                new ServiceAdvisorProfileNotFoundException(
                        "Service advisor profile not found"
                );

        ResponseEntity<ApiResponse<Void>> response =
                exceptionHandler.handleUserNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(
                "NOT_FOUND",
                response.getBody().getStatus()
        );
        assertEquals(
                "Service advisor profile not found",
                response.getBody().getMessage()
        );
        assertNull(response.getBody().getData());
    }
}