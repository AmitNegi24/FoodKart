package com.foodkart.delivery_service.kafka;

import com.foodkart.delivery_service.dto.DeliveryStatusUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryStatusEventProducer {

    private static final String TOPIC = "delivery-status-updated";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishStatusUpdate(Long orderId, String status) {
        try {
            DeliveryStatusUpdatedEvent event =
                    new DeliveryStatusUpdatedEvent(orderId, status);

            String message = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(
                    TOPIC,
                    orderId.toString(),
                    message
            ).whenComplete((result, exception) -> {
                if (exception != null) {
                    log.error(
                            "Failed to publish delivery status for order {}",
                            orderId,
                            exception
                    );
                } else {
                    log.info(
                            "Published delivery status event for order {}: {}",
                            orderId,
                            status
                    );
                }
            });

        } catch (Exception e) {
            log.error("Could not serialize delivery status event", e);
            throw new IllegalStateException(
                    "Failed to create delivery status event", e
            );
        }
    }
}
