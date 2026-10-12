
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

    private final ObjectMapper objectMapper;
    private final DeliveryService deliveryService;

    @KafkaListener(
            topics = "payment-success",
            groupId = "delivery-service-group"
    )
    public void consumePaymentSuccess(String message) {

        try {
            PaymentSuccessEvent event =
                    objectMapper.readValue(
                            message,
                            PaymentSuccessEvent.class
                    );

            log.info(
                    "Received payment-success event for order {}",
                    event.getOrderId()
            );

            deliveryService.createDelivery(event.getOrderId());

            log.info(
                    "Delivery created or already exists for order {}",
                    event.getOrderId()
            );

        } catch (Exception e) {
            log.error(
                    "Failed to process payment-success event: {}",
                    message,
                    e
            );

            throw new RuntimeException(
                    "Failed to process payment-success event",
                    e
            );
        }
    }
}
