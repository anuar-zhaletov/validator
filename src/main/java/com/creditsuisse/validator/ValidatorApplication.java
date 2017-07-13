package com.creditsuisse.validator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main Configuration
 */
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.creditsuisse.validator")
public class ValidatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ValidatorApplication.class, args);
	}
}
