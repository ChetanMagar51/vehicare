package com.vehicare.modules.inventory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.vehicare.common.api.ApiResponse;

@RestControllerAdvice
public class InventoryExceptionHandler {

	@ExceptionHandler(PartNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handlePartNotFound(PartNotFoundException ex) {

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Error", ex.getMessage(), null));
	}

	@ExceptionHandler(InvalidStockQuantityException.class)
	public ResponseEntity<ApiResponse<Void>> handleInvalidStockQuantity(InvalidStockQuantityException ex) {

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>("Error", ex.getMessage(), null));
	}

	@ExceptionHandler(InsufficientStockException.class)
	public ResponseEntity<ApiResponse<Void>> handleInsufficientStock(InsufficientStockException ex) {

		return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>("Error", ex.getMessage(), null));
	}
}