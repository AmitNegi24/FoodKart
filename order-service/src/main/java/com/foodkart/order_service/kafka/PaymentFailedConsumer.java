package com.foodkart.order_service.kafka;

import com.foodkart.order_service.dto.PaymentFailedEvent;
import com.foodkart.order_service.entity.Order;
import com.foodkart.order_service.entity.OrderStatus;
import com.foodkart.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class PaymentFailedConsumer {

    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;

    @KafkaListener(
            topics = "payment-failed",
            groupId = "order-payment-failed-group"
    )
    public void consumePaymentFailed(String message) {

        try {

            System.out.println("=================================");
            System.out.println("PAYMENT FAILED CONSUMER CALLED");
            System.out.println("Received message: " + message);

            PaymentFailedEvent event =
                    objectMapper.readValue(
                            message,
                            PaymentFailedEvent.class
                    );

            Order order = orderRepository
                    .findById(event.getOrderId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Order not found: "
                                            + event.getOrderId()
                            )
                    );

            order.setStatus(OrderStatus.CANCELLED);

            orderRepository.save(order);

            System.out.println(
                    "Order "
                            + order.getId()
                            + " cancelled because payment failed."
            );

            System.out.println(
                    "Reason: " + event.getReason()
            );

            System.out.println("=================================");

        } catch (Exception e) {

            System.err.println(
                    "Failed to process PAYMENT_FAILED event"
            );

            e.printStackTrace();
        }
    }
}