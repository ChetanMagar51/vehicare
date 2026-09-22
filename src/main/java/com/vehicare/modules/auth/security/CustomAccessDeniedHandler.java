package com.vehicare.modules.auth.security;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicare.common.api.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	private final ObjectMapper objectMapper;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException {

		ApiResponse<Void> apiResponse = ApiResponse.<Void>builder().status("Forbidden")
				.message("You do not have permission to access this resource").data(null).build();

		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType("application/json");

		objectMapper.writeValue(response.getWriter(), apiResponse);
	}
}