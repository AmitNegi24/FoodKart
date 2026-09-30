package com.foodkart.order_service.kafka;

import com.foodkart.order_service.dto.PaymentSuccessEvent;
import com.foodkart.order_service.entity.Order;
import com.foodkart.order_service.entity.OrderStatus;
import com.foodkart.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class PaymentSuccessConsumer {

    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;

    @KafkaListener(
            topics = "payment-success",
            groupId = "order-payment-group"
    )
    public void consumePaymentSuccess(String message) {

        try {

            System.out.println("=================================");
            System.out.println("PAYMENT SUCCESS CONSUMER CALLED");
            System.out.println("Received message: " + message);

            PaymentSuccessEvent event =
                    objectMapper.readValue(
                            message,
                            PaymentSuccessEvent.class
                    );

            Order order = orderRepository
                    .findById(event.getOrderId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Order not found: "
                                            + event.getOrderId()
                            )
                    );

            order.setStatus(OrderStatus.CONFIRMED);

            orderRepository.save(order);

            System.out.println(
                    "Order "
                            + order.getId()
                            + " confirmed successfully."
            );

            System.out.println("=================================");

        } catch (Exception e) {

            System.err.println(
                    "Failed to process PAYMENT_SUCCESS event"
            );
            throw new RuntimeException(
                    "Failed to process PAYMENT_SUCCESS",
                    e
            );
        }
    }
}