
package com.foodkart.delivery_service.kafka;

import com.foodkart.delivery_service.dto.PaymentSuccessEvent;
import com.foodkart.delivery_service.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentSuccessConsumer {

    private final DeliveryService deliveryService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "payment-success",
            groupId = "delivery-service-group"
    )
    public void consumePaymentSuccess(String payload) {

        try {
            PaymentSuccessEvent event =
                    objectMapper.readValue(
                            payload,
                            PaymentSuccessEvent.class
                    );

            log.info(
                    "Received PAYMENT_SUCCESS for orderId={}, correlationId={}",
                    event.getOrderId(),
                    event.getCorrelationId()
            );

            deliveryService.createDelivery(event.getOrderId());

            log.info(
                    "Delivery created for orderId={}",
                    event.getOrderId()
            );

        } catch (Exception e) {
            log.error("Failed to process PAYMENT_SUCCESS event", e);
            throw new RuntimeException(
                    "Failed to process payment success event",
                    e
            );
        }
    }
}