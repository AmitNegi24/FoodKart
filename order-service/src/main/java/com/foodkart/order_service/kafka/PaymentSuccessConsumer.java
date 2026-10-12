package com.foodkart.order_service.kafka;

import com.foodkart.order_service.dto.PaymentSuccessEvent;
import com.foodkart.order_service.entity.Order;
import com.foodkart.order_service.entity.OrderStatus;
import com.foodkart.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentSuccessConsumer {

    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;

    @KafkaListener(
            topics = "payment-success",
            groupId = "order-payment-group"
    )
    @Transactional
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
            // Duplicate event: the order is already confirmed.
            if (order.getStatus() == OrderStatus.CONFIRMED) {
                log.info(
                        "Order {} is already confirmed. Ignoring duplicate payment event.",
                        order.getId()
                );
                return;
            }
            // Never confirm an order that is not awaiting payment.
            if (order.getStatus() != OrderStatus.PENDING) {
                log.warn(
                        "Ignoring payment-success event for order {} with status {}",
                        order.getId(),
                        order.getStatus()
                );
                return;
            }

            order.setStatus(OrderStatus.CONFIRMED);

            orderRepository.save(order);

            log.info(
                    "Order {} confirmed successfully after payment success.",
                    order.getId()
            );

            System.out.println("=================================");

        } catch (Exception e) {

            log.error(
                    "Failed to process PAYMENT_SUCCESS event. Message: {}",
                    message,
                    e
            );
            throw new RuntimeException(
                    "Failed to process PAYMENT_SUCCESS",
                    e
            );
        }
    }
}