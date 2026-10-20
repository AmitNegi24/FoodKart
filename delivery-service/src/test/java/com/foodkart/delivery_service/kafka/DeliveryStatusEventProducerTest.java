package com.foodkart.delivery_service.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DeliveryStatusEventProducerTest {

    private KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;
    private DeliveryStatusEventProducer deliveryStatusEventProducer;

    @BeforeEach
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        objectMapper = mock(ObjectMapper.class);
        deliveryStatusEventProducer = new DeliveryStatusEventProducer(kafkaTemplate, objectMapper);
    }

    @Test
    void testPublishStatusUpdate_success() throws Exception {
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"orderId\":101,\"status\":\"DELIVERED\"}");
        when(kafkaTemplate.send(eq("delivery-status-updated"), eq("101"), any(String.class)))
                .thenReturn(mock(java.util.concurrent.CompletableFuture.class));

        assertDoesNotThrow(() -> deliveryStatusEventProducer.publishStatusUpdate(101L, "DELIVERED"));

        verify(kafkaTemplate, times(1)).send(eq("delivery-status-updated"), eq("101"), any(String.class));
    }

    @Test
    void testPublishStatusUpdate_differentStatus() throws Exception {
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"orderId\":202,\"status\":\"PICKED_UP\"}");
        when(kafkaTemplate.send(eq("delivery-status-updated"), eq("202"), any(String.class)))
                .thenReturn(mock(java.util.concurrent.CompletableFuture.class));

        assertDoesNotThrow(() -> deliveryStatusEventProducer.publishStatusUpdate(202L, "PICKED_UP"));

        verify(kafkaTemplate, times(1)).send(eq("delivery-status-updated"), eq("202"), any(String.class));
    }

    @Test
    void testPublishStatusUpdate_kafkaException() throws Exception {
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"orderId\":101,\"status\":\"DELIVERED\"}");
        when(kafkaTemplate.send(eq("delivery-status-updated"), eq("101"), any(String.class)))
                .thenReturn(mock(java.util.concurrent.CompletableFuture.class));

        assertDoesNotThrow(() -> deliveryStatusEventProducer.publishStatusUpdate(101L, "DELIVERED"));
    }

    @Test
    void testPublishStatusUpdate_serializationException() throws Exception {
        when(objectMapper.writeValueAsString(any())).thenThrow(new RuntimeException("Serialization error"));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> deliveryStatusEventProducer.publishStatusUpdate(101L, "DELIVERED")
        );

        assertTrue(exception.getMessage().contains("Failed to create delivery status event"));
    }
}
