
package com.foodkart.delivery_service.kafka;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DeliveryStatusEventProducerTest {

    private KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;
    private DeliveryStatusEventProducer producer;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        objectMapper = mock(ObjectMapper.class);
        producer = new DeliveryStatusEventProducer(
                kafkaTemplate, objectMapper
        );
    }

    @Test
    void testPublishStatusUpdate_success() throws Exception {

        when(objectMapper.writeValueAsString(any()))
                .thenReturn("{\"orderId\":101,\"status\":\"DELIVERED\"}");

        when(kafkaTemplate.send(
                eq("delivery-status-updated"),
                eq("101"),
                anyString()
        )).thenReturn(CompletableFuture.completedFuture(null));

        assertDoesNotThrow(() ->
                producer.publishStatusUpdate(101L, "DELIVERED")
        );

        verify(kafkaTemplate).send(
                eq("delivery-status-updated"),
                eq("101"),
                anyString()
        );
    }

    @Test
    void testPublishStatusUpdate_differentStatus() throws Exception {

        when(objectMapper.writeValueAsString(any()))
                .thenReturn("{\"orderId\":202,\"status\":\"PICKED_UP\"}");

        when(kafkaTemplate.send(
                eq("delivery-status-updated"),
                eq("202"),
                anyString()
        )).thenReturn(CompletableFuture.completedFuture(null));

        assertDoesNotThrow(() ->
                producer.publishStatusUpdate(202L, "PICKED_UP")
        );

        verify(kafkaTemplate).send(
                eq("delivery-status-updated"),
                eq("202"),
                anyString()
        );
    }

    @Test
    void testPublishStatusUpdate_kafkaException() throws Exception {

        when(objectMapper.writeValueAsString(any()))
                .thenReturn("{\"orderId\":101,\"status\":\"DELIVERED\"}");

        CompletableFuture<org.springframework.kafka.support.SendResult<String, String>>
                failedFuture = new CompletableFuture<>();

        failedFuture.completeExceptionally(
                new RuntimeException("Kafka unavailable")
        );

        when(kafkaTemplate.send(
                eq("delivery-status-updated"),
                eq("101"),
                anyString()
        )).thenReturn(failedFuture);

        // Producer logs asynchronous failures.
        assertDoesNotThrow(() ->
                producer.publishStatusUpdate(101L, "DELIVERED")
        );

        verify(kafkaTemplate).send(
                eq("delivery-status-updated"),
                eq("101"),
                anyString()
        );
    }

    @Test
    void testPublishStatusUpdate_serializationException() throws Exception {

        when(objectMapper.writeValueAsString(any()))
                .thenThrow(new RuntimeException("Serialization error"));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> producer.publishStatusUpdate(101L, "DELIVERED")
        );

        assertTrue(
                exception.getMessage().contains(
                        "Failed to create delivery status event"
                )
        );

        verifyNoInteractions(kafkaTemplate);
    }
}
