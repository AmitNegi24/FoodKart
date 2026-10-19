package com.foodkart.order_service.kafka;

import com.foodkart.order_service.dto.OrderCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEventProducerTest {

    @Mock
    private KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    @InjectMocks
    private OrderEventProducer orderEventProducer;

    private OrderCreatedEvent orderCreatedEvent;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(orderEventProducer, "topicName", "order-topic");

        orderCreatedEvent = OrderCreatedEvent.builder()
                .orderId(1L)
                .userEmailId("test@example.com")
                .amount(new BigDecimal("20.00"))
                .correlationId("correlation-123")
                .build();
    }

    @Test
    void publishOrderCreated_success() {
        orderEventProducer.publishOrderCreated(orderCreatedEvent);

        verify(kafkaTemplate, times(1)).send(eq("order-topic"), eq("1"), eq(orderCreatedEvent));
    }

    @Test
    void publishOrderCreated_withDifferentOrderId() {
        orderCreatedEvent.setOrderId(999L);
        orderEventProducer.publishOrderCreated(orderCreatedEvent);

        verify(kafkaTemplate, times(1)).send(eq("order-topic"), eq("999"), eq(orderCreatedEvent));
    }
}
