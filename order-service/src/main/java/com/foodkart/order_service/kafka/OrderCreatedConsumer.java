package com.foodkart.order_service.kafka;

import com.foodkart.order_service.dto.OrderCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class OrderCreatedConsumer {

    private final ObjectMapper objectMapper;

    public OrderCreatedConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "order-created",
            groupId = "order-service-group"
    )
    public void consumeOrderCreated(String message) {

        try {

            System.out.println("CONSUMER CALLED");
            System.out.println("Received message: " + message);

            OrderCreatedEvent event =
                    objectMapper.readValue(
                            message,
                            OrderCreatedEvent.class
                    );

            System.out.println("Order ID: " + event.getOrderId());
            System.out.println("User Email: " + event.getUserEmailId());
            System.out.println("Amount: " + event.getAmount());

        } catch (Exception e) {

            System.err.println(
                    "Failed to process ORDER_CREATED event"
            );

            e.printStackTrace();
        }
    }
}