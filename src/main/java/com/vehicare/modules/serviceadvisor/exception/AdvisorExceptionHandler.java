package com.vehicare.modules.serviceadvisor.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.vehicare.common.api.ApiResponse;
import com.vehicare.modules.user.exception.UserNotFoundException;

@RestControllerAdvice
public class AdvisorExceptionHandler {
	
	@ExceptionHandler(ServiceAdvisorProfileAlreadyExistsException.class)
	public ResponseEntity<ApiResponse<Void>> profileAlreadyExistsException(ServiceAdvisorProfileAlreadyExistsException ex)
	{
		 ApiResponse<Void> response = ApiResponse.<Void>builder()
	                .status("Failed")
	                .message(ex.getMessage())
	                .data(null)
	                .build();

	        return ResponseEntity
	                .status(HttpStatus.CONFLICT)
	                .body(response);
	}
	
	
	@ExceptionHandler(ServiceAdvisorProfileNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFoundException(ServiceAdvisorProfileNotFoundException ex)
    {


    return	new ResponseEntity<>(ApiResponse.<Void>builder()
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.name())
                .build(), HttpStatus.NOT_FOUND);

    }
	

}
