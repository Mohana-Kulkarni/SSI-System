package com.example.ssisystem;

import io.swagger.v3.oas.models.annotations.OpenAPI30;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@OpenAPI30
@SpringBootApplication(scanBasePackages = "com.example.ssisystem")
public class SsiSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(SsiSystemApplication.class, args);
	}

}
