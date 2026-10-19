
package com.foodkart.order_service.kafka;

import tools.jackson.databind.ObjectMapper;
import com.foodkart.order_service.dto.OrderCreatedEvent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderCreatedConsumerTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrderCreatedConsumer orderCreatedConsumer;

    @Test
    void consumeOrderCreated_success() {

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(1L)
                .userEmailId("test@example.com")
                .amount(new BigDecimal("20.00"))
                .correlationId("correlation-123")
                .build();

        when(objectMapper.readValue(
                anyString(),
                eq(OrderCreatedEvent.class)
        )).thenReturn(event);

        assertDoesNotThrow(() ->
                orderCreatedConsumer.consumeOrderCreated(
                        "{\"orderId\":1}"
                )
        );

        verify(objectMapper, times(1))
                .readValue(anyString(), eq(OrderCreatedEvent.class));
    }

    @Test
    void consumeOrderCreated_exceptionHandling() {

        when(objectMapper.readValue(
                anyString(),
                eq(OrderCreatedEvent.class)
        )).thenThrow(
                new RuntimeException("Deserialization error")
        );

        // Assumes the consumer handles the exception internally.
        assertDoesNotThrow(() ->
                orderCreatedConsumer.consumeOrderCreated(
                        "{\"orderId\":1}"
                )
        );

        verify(objectMapper, times(1))
                .readValue(anyString(), eq(OrderCreatedEvent.class));
    }
}
