package com.vehicare.modules.email.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.vehicare.common.api.ApiResponse;
@RestControllerAdvice
public class EmailExceptionHandler {
	
	
	@ExceptionHandler(EmailSendingException.class)
	public ResponseEntity<ApiResponse<Void>> handleEmailSendingException(
	        EmailSendingException exception) {

	    ApiResponse<Void> response = ApiResponse.<Void>builder()
	            .status("ERROR")
	            .message(exception.getMessage())
	            .data(null)
	            .build();

	    return ResponseEntity
	            .status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body(response);
	}

}
