package com.vehicare.modules.inventory.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.vehicare.common.api.ApiResponse;

class InventoryExceptionHandlerTest {

	private InventoryExceptionHandler exceptionHandler;

	@BeforeEach
	void setUp() {
		exceptionHandler = new InventoryExceptionHandler();
	}

	@Test
	void handlePartNotFound_shouldReturnNotFoundResponse() {

		PartNotFoundException exception = new PartNotFoundException("Part not found with id: 1");

		ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handlePartNotFound(exception);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

		ApiResponse<Void> body = response.getBody();

		assertEquals("Error", body.getStatus());
		assertEquals("Part not found with id: 1", body.getMessage());
		assertNull(body.getData());
	}

	@Test
	void handleInvalidStockQuantity_shouldReturnBadRequestResponse() {

		InvalidStockQuantityException exception = new InvalidStockQuantityException(
				"Quantity must be greater than zero");

		ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleInvalidStockQuantity(exception);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

		ApiResponse<Void> body = response.getBody();

		assertEquals("Error", body.getStatus());
		assertEquals("Quantity must be greater than zero", body.getMessage());
		assertNull(body.getData());
	}

	@Test
	void handleInsufficientStock_shouldReturnConflictResponse() {

		InsufficientStockException exception = new InsufficientStockException(
				"Insufficient stock for part: Brake Pad. Available: 5, requested: 10");

		ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleInsufficientStock(exception);

		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

		ApiResponse<Void> body = response.getBody();

		assertEquals("Error", body.getStatus());
		assertEquals("Insufficient stock for part: Brake Pad. Available: 5, requested: 10", body.getMessage());
		assertNull(body.getData());
	}
}