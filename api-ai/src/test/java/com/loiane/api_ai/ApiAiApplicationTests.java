package com.loiane.api_ai;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@SpringBootTest
@ComponentScan(basePackages = {"com.loiane.api_ai.chat"})
class ApiAiApplicationTests {

	@Test
	void contextLoads() {
	}

}
