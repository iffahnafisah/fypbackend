package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan(basePackages = "com.example.demo")
@ComponentScan(basePackages = "com.example.demo")
public class FypwebApplication {

	public static void main(String[] args) {
		SpringApplication.run(FypwebApplication.class, args);
	}

}