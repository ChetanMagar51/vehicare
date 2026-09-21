package com.vehicare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VehicareBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(VehicareBackendApplication.class, args);
	}

}
