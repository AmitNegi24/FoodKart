package com.foodkart.order_service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Skipping SpringBootTest as it requires external dependencies (Kafka, Database)")
class OrderServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
