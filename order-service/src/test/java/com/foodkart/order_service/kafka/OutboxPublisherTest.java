package com.foodkart.order_service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import tools.jackson.databind.ObjectMapper;
import com.foodkart.order_service.dto.OrderCreatedEvent;
import com.foodkart.order_service.entity.OutboxEvent;
import com.foodkart.order_service.entity.OutboxStatus;
import com.foodkart.order_service.repository.OutboxEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxPublisherTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CompletableFuture<SendResult<String, Object>> future;

    @InjectMocks
    private OutboxPublisher outboxPublisher;

    private OutboxEvent outboxEvent;
    private OrderCreatedEvent orderCreatedEvent;

    @BeforeEach
    void setUp() throws Exception {
        outboxEvent = OutboxEvent.builder()
                .id(1L)
                .aggregateType("ORDER")
                .aggregateId("1")
                .eventType("ORDER_CREATED")
                .payload("{\"orderId\":1,\"userEmailId\":\"test@example.com\",\"amount\":20.00}")
                .status(OutboxStatus.PENDING)
                .build();

        orderCreatedEvent = OrderCreatedEvent.builder()
                .orderId(1L)
                .userEmailId("test@example.com")
                .amount(new BigDecimal("20.00"))
                .correlationId("correlation-123")
                .build();

        lenient().when(kafkaTemplate.send(eq("order-created"), eq("1"), any(OrderCreatedEvent.class)))
                .thenReturn(future);
        lenient().when(future.get()).thenReturn(mock(SendResult.class));
    }

    @Test
    void publishPendingEvents_success() throws Exception {
        when(outboxEventRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING))
                .thenReturn(List.of(outboxEvent));
        when(objectMapper.readValue(any(String.class), eq(OrderCreatedEvent.class)))
                .thenReturn(orderCreatedEvent);

        outboxPublisher.publishPendingEvents();

        verify(outboxEventRepository, times(1)).findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        verify(objectMapper, times(1)).readValue(any(String.class), eq(OrderCreatedEvent.class));
        verify(kafkaTemplate, times(1)).send(eq("order-created"), eq("1"), any(OrderCreatedEvent.class));
        verify(outboxEventRepository, times(1)).save(any(OutboxEvent.class));
        assertEquals(OutboxStatus.PROCESSED, outboxEvent.getStatus());
    }

    @Test
    void publishPendingEvents_noPendingEvents() throws JsonProcessingException {
        when(outboxEventRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING))
                .thenReturn(Collections.emptyList());

        outboxPublisher.publishPendingEvents();

        verify(outboxEventRepository, times(1)).findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        verify(objectMapper, never()).readValue(any(String.class), any(Class.class));
        verify(kafkaTemplate, never()).send(any(), any(), any());
        verify(outboxEventRepository, never()).save(any(OutboxEvent.class));
    }

    @Test
    void publishPendingEvents_exceptionHandling() throws Exception {
        when(outboxEventRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING))
                .thenReturn(List.of(outboxEvent));
        when(objectMapper.readValue(any(String.class), eq(OrderCreatedEvent.class)))
                .thenThrow(new RuntimeException("Deserialization error"));

        outboxPublisher.publishPendingEvents();

        verify(outboxEventRepository, times(1)).findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        verify(objectMapper, times(1)).readValue(any(String.class), eq(OrderCreatedEvent.class));
        verify(kafkaTemplate, never()).send(any(), any(), any());
        verify(outboxEventRepository, never()).save(any(OutboxEvent.class));
    }
}
