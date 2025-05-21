package com.loiane.api_ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.loiane.api_ai", "com.loiane.api_ai.rag"})
public class ApiAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiAiApplication.class, args);
	}

}
