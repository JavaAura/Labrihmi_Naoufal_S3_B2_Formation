package com.formation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FormationApplication {

	public static void main(String[] args) {
		SpringApplication.run(FormationApplication.class, args);
		System.out.println("Swagger documentation available at: http://localhost:8080/swagger-ui/index.html");
	}

}
