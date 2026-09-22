package com.vehicare.modules.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.vehicare.modules.auth.security.CustomAccessDeniedHandler;
import com.vehicare.modules.auth.security.CustomAuthenticationEntryPoint;
import com.vehicare.modules.auth.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final AuthenticationProvider authenticationProvider;

	private final CustomAuthenticationEntryPoint authenticationEntryPoint;
	private final CustomAccessDeniedHandler accessDeniedHandler;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable())

				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				.authorizeHttpRequests(auth -> auth

						.requestMatchers("/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**", "/auth/**")
						.permitAll().requestMatchers("/admin/**").hasRole("Admin")

						.requestMatchers("/users/**").authenticated()

						.anyRequest().authenticated())

				.authenticationProvider(authenticationProvider)

				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

				.exceptionHandling(exception -> exception

						.authenticationEntryPoint(authenticationEntryPoint)

						.accessDeniedHandler(accessDeniedHandler));

		return http.build();
	}
}
