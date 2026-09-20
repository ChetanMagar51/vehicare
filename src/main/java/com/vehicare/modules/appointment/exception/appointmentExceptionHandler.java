package com.vehicare.modules.appointment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.vehicare.common.api.ApiResponse;

@RestControllerAdvice
public class appointmentExceptionHandler {

	@ExceptionHandler(AppointmentNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> profileAlreadyExistsException(AppointmentNotFoundException ex) {
		ApiResponse<Void> response = ApiResponse.<Void>builder().status("Failed").message(ex.getMessage()).data(null)
				.build();

		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}

}
