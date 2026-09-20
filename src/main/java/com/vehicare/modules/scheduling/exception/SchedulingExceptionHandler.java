package com.vehicare.modules.scheduling.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.vehicare.common.api.ApiResponse;

@RestControllerAdvice
public class SchedulingExceptionHandler {

	@ExceptionHandler(WorkingHoursNotConfiguredException.class)
	public ResponseEntity<ApiResponse<Void>> handleWorkingHoursNotConfigured(WorkingHoursNotConfiguredException ex) {

		ApiResponse<Void> response = ApiResponse.<Void>builder().status("ERROR").message(ex.getMessage()).data(null)
				.build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler(ServiceSlotNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleServiceSlotNotFound(ServiceSlotNotFoundException ex) {

		ApiResponse<Void> response = ApiResponse.<Void>builder().status("ERROR").message(ex.getMessage()).data(null)
				.build();

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	@ExceptionHandler(ServiceSlotAlreadyBookedException.class)
	public ResponseEntity<ApiResponse<Void>> handleServiceSlotAlreadyBooked(ServiceSlotAlreadyBookedException ex) {

		ApiResponse<Void> response = ApiResponse.<Void>builder().status("ERROR").message(ex.getMessage()).data(null)
				.build();

		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}

	@ExceptionHandler(InvalidSlotException.class)
	public ResponseEntity<ApiResponse<Void>> handleInvalidSlot(InvalidSlotException ex) {

		ApiResponse<Void> response = ApiResponse.<Void>builder().status("ERROR").message(ex.getMessage()).data(null)
				.build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
}