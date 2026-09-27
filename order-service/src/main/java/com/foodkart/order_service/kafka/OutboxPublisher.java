package com.foodkart.order_service.kafka;

import com.foodkart.order_service.dto.OrderCreatedEvent;
import com.foodkart.order_service.entity.OutboxEvent;
import com.foodkart.order_service.entity.OutboxStatus;
import com.foodkart.order_service.repository.OutboxEventRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {

        List<OutboxEvent> events =
                outboxEventRepository
                        .findByStatusOrderByCreatedAtAsc(
                                OutboxStatus.PENDING
                        );

        for (OutboxEvent outboxEvent : events) {

            try {

                OrderCreatedEvent event =
                        objectMapper.readValue(
                                outboxEvent.getPayload(),
                                OrderCreatedEvent.class
                        );

                kafkaTemplate.send(
                        "order-created",
                        outboxEvent.getAggregateId(),
                        event
                ).get();

                outboxEvent.setStatus(
                        OutboxStatus.PROCESSED
                );

                outboxEventRepository.save(outboxEvent);

                System.out.println(
                        "Outbox event published successfully. ID: "
                                + outboxEvent.getId()
                );

            } catch (Exception e) {

                System.err.println(
                        "Failed to publish outbox event ID: "
                                + outboxEvent.getId()
                );

                e.printStackTrace();
            }
        }
    }
}