package com.vehicare.modules.serviceoperations.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.vehicare.common.api.ApiResponse;

class ServiceOperationsExceptionHandlerTest {

	private ServiceOperationsExceptionHandler exceptionHandler;

	@BeforeEach
	void setUp() {
		exceptionHandler = new ServiceOperationsExceptionHandler();
	}

	// =========================================================
	// ServiceRecordNotFoundException
	// =========================================================

	@Test
	void handleServiceRecordNotFound_shouldReturnNotFoundResponse() {

		ServiceRecordNotFoundException exception = new ServiceRecordNotFoundException(
				"Service record not found with ID: 1");

		ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleServiceRecordNotFound(exception);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

		ApiResponse<Void> body = response.getBody();

		assertNotNull(body);
		assertEquals("ERROR", body.getStatus());
		assertEquals("Service record not found with ID: 1", body.getMessage());
		assertNull(body.getData());
	}

	// =========================================================
	// ServiceTaskNotFoundException
	// =========================================================

	@Test
	void handleServiceTaskNotFound_shouldReturnNotFoundResponse() {

		ServiceTaskNotFoundException exception = new ServiceTaskNotFoundException("Service task not found with ID: 10");

		ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleServiceTaskNotFound(exception);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

		ApiResponse<Void> body = response.getBody();

		assertNotNull(body);
		assertEquals("ERROR", body.getStatus());
		assertEquals("Service task not found with ID: 10", body.getMessage());
		assertNull(body.getData());
	}

	// =========================================================
	// ServiceRecordAlreadyExistsException
	// =========================================================

	@Test
	void handleServiceRecordAlreadyExists_shouldReturnConflictResponse() {

		ServiceRecordAlreadyExistsException exception = new ServiceRecordAlreadyExistsException(
				"Service record already exists for appointment ID: 101");

		ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleServiceRecordAlreadyExists(exception);

		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

		ApiResponse<Void> body = response.getBody();

		assertNotNull(body);
		assertEquals("ERROR", body.getStatus());
		assertEquals("Service record already exists for appointment ID: 101", body.getMessage());
		assertNull(body.getData());
	}

	// =========================================================
	// InvalidServiceStatusException
	// =========================================================

	@Test
	void handleInvalidServiceStatus_shouldReturnConflictResponse() {

		InvalidServiceStatusException exception = new InvalidServiceStatusException(
				"Service can only be completed when its status is IN_PROGRESS.");

		ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleInvalidServiceStatus(exception);

		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

		ApiResponse<Void> body = response.getBody();

		assertNotNull(body);
		assertEquals("ERROR", body.getStatus());
		assertEquals("Service can only be completed when its status is IN_PROGRESS.", body.getMessage());
		assertNull(body.getData());
	}

	// =========================================================
	// InvalidServiceTaskStatusException
	// =========================================================

	@Test
	void handleInvalidServiceTaskStatus_shouldReturnConflictResponse() {

		InvalidServiceTaskStatusException exception = new InvalidServiceTaskStatusException(
				"Task can only be started when its status is PENDING.");

		ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleInvalidServiceTaskStatus(exception);

		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

		ApiResponse<Void> body = response.getBody();

		assertNotNull(body);
		assertEquals("ERROR", body.getStatus());
		assertEquals("Task can only be started when its status is PENDING.", body.getMessage());
		assertNull(body.getData());
	}

	// =========================================================
	// ServiceOperationsException
	// =========================================================

	@Test
	void handleServiceOperationsException_shouldReturnBadRequestResponse() {

		ServiceOperationsException exception = new ServiceOperationsException("Invalid service operation.");

		ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleServiceOperationsException(exception);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

		ApiResponse<Void> body = response.getBody();

		assertNotNull(body);
		assertEquals("ERROR", body.getStatus());
		assertEquals("Invalid service operation.", body.getMessage());
		assertNull(body.getData());
	}
}