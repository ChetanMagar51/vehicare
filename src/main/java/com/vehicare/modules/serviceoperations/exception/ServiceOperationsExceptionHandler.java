package com.vehicare.modules.serviceoperations.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.vehicare.common.api.ApiResponse;

@RestControllerAdvice
public class ServiceOperationsExceptionHandler {

	@ExceptionHandler(ServiceRecordNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleServiceRecordNotFound(ServiceRecordNotFoundException ex) {

		return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ServiceTaskNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleServiceTaskNotFound(ServiceTaskNotFoundException ex) {

		return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ServiceRecordAlreadyExistsException.class)
	public ResponseEntity<ApiResponse<Void>> handleServiceRecordAlreadyExists(ServiceRecordAlreadyExistsException ex) {

		return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(InvalidServiceStatusException.class)
	public ResponseEntity<ApiResponse<Void>> handleInvalidServiceStatus(InvalidServiceStatusException ex) {

		return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(InvalidServiceTaskStatusException.class)
	public ResponseEntity<ApiResponse<Void>> handleInvalidServiceTaskStatus(InvalidServiceTaskStatusException ex) {

		return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(ServiceOperationsException.class)
	public ResponseEntity<ApiResponse<Void>> handleServiceOperationsException(ServiceOperationsException ex) {

		return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	private ResponseEntity<ApiResponse<Void>> buildErrorResponse(String message, HttpStatus status) {

		ApiResponse<Void> response = ApiResponse.<Void>builder().status("ERROR").message(message).data(null).build();

		return ResponseEntity.status(status).body(response);
	}
}