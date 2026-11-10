
package com.foodkart.delivery_service.kafka;

import com.foodkart.delivery_service.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentSuccessConsumer {

    private final ObjectMapper objectMapper;
    private final DeliveryService deliveryService;

    @KafkaListener(
            topics = "order-ready-for-pickup",
            groupId = "delivery-ready-for-pickup-group"
    )
    public void consumeOrderReadyForPickup(String message) {

        try {
            JsonNode event = objectMapper.readTree(message);

            long orderId = event.get("orderId").asLong();
            String status = event.get("status").asString();

            if (!"READY_FOR_PICKUP".equals(status)) {
                log.warn(
                        "Ignoring unexpected order status {} for order {}",
                        status,
                        orderId
                );
                return;
            }

            deliveryService.createDelivery(orderId);

            log.info(
                    "Order {} is ready for pickup. Delivery is available for assignment.",
                    orderId
            );

        } catch (Exception e) {
            log.error(
                    "Failed to process order-ready-for-pickup event: {}",
                    message,
                    e
            );

            throw new IllegalStateException(
                    "Failed to process order-ready-for-pickup event",
                    e
            );
        }
    }
}
