
package com.foodkart.order_service.kafka;

import com.foodkart.order_service.dto.DeliveryStatusUpdatedEvent;
import com.foodkart.order_service.entity.Order;
import com.foodkart.order_service.entity.OrderStatus;
import com.foodkart.order_service.repository.OrderRepository;
import com.foodkart.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryStatusUpdatedConsumer {

    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @KafkaListener(
            topics = "delivery-status-updated",
            groupId = "order-delivery-group"
    )
    public void consumeDeliveryStatusUpdated(String message) {
        try {
            DeliveryStatusUpdatedEvent event =
                    objectMapper.readValue(
                            message,
                            DeliveryStatusUpdatedEvent.class
                    );

            Order order = orderRepository.findById(event.getOrderId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Order not found: " + event.getOrderId()
                    ));

            OrderStatus targetStatus = switch (event.getStatus()) {
                case "PICKED_UP" -> OrderStatus.OUT_FOR_DELIVERY;
                case "DELIVERED" -> OrderStatus.DELIVERED;
                default -> null;
            };

            // These delivery events do not change OrderStatus.
            if (targetStatus == null) {
                log.info(
                        "No order status update required for delivery status {}",
                        event.getStatus()
                );
                return;
            }

            // Ignore duplicate events that have already been applied.
            if (order.getStatus() == targetStatus) {
                log.info(
                        "Order {} already has status {}",
                        order.getId(),
                        targetStatus
                );
                return;
            }

            orderService.updateOrderStatus(
                    event.getOrderId(),
                    targetStatus
            );

            log.info(
                    "Order {} updated to {} after delivery event",
                    event.getOrderId(),
                    targetStatus
            );

        } catch (Exception e) {
            log.error(
                    "Failed to process delivery status event: {}",
                    message,
                    e
            );
            throw new IllegalStateException(
                    "Failed to process delivery status event", e
            );
        }
    }
}
