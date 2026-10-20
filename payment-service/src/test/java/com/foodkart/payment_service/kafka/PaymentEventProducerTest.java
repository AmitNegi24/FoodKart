package com.foodkart.payment_service.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentEventProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private CompletableFuture<SendResult<String, Object>> future;

    @InjectMocks
    private PaymentEventProducer paymentEventProducer;

    @BeforeEach
    void setUp() throws Exception {
        lenient().when(kafkaTemplate.send(eq("payment-success"), eq("1"), any(String.class)))
                .thenReturn(future);
        lenient().when(future.get()).thenReturn(mock(SendResult.class));
        lenient().when(kafkaTemplate.send(eq("payment-failed"), eq("1"), any(String.class)))
                .thenReturn(future);
    }

    @Test
    void publishPaymentSuccess_success() throws Exception {
        when(kafkaTemplate.send(eq("payment-success"), eq("1"), any(String.class)))
                .thenReturn(future);
        when(future.get()).thenReturn(mock(SendResult.class));

        assertDoesNotThrow(() -> 
            paymentEventProducer.publishPaymentSuccess("{\"test\":\"data\"}", "1")
        );

        verify(kafkaTemplate, times(1)).send(eq("payment-success"), eq("1"), any(String.class));
    }

    @Test
    void publishPaymentSuccess_differentOrderId() throws Exception {
        when(kafkaTemplate.send(eq("payment-success"), eq("2"), any(String.class)))
                .thenReturn(future);
        when(future.get()).thenReturn(mock(SendResult.class));

        assertDoesNotThrow(() -> 
            paymentEventProducer.publishPaymentSuccess("{\"test\":\"data\"}", "2")
        );

        verify(kafkaTemplate, times(1)).send(eq("payment-success"), eq("2"), any(String.class));
    }

    @Test
    void publishPaymentSuccess_exceptionHandling() throws Exception {
        when(kafkaTemplate.send(eq("payment-success"), eq("1"), any(String.class)))
                .thenReturn(future);
        when(future.get()).thenThrow(new RuntimeException("Kafka error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            paymentEventProducer.publishPaymentSuccess("{\"test\":\"data\"}", "1")
        );

        assertTrue(exception.getMessage().contains("Failed to publish PAYMENT_SUCCESS event"));
        assertNotNull(exception.getCause());
    }

    @Test
    void publishPaymentFailed_success() throws Exception {
        when(kafkaTemplate.send(eq("payment-failed"), eq("1"), any(String.class)))
                .thenReturn(future);
        when(future.get()).thenReturn(mock(SendResult.class));

        assertDoesNotThrow(() -> 
            paymentEventProducer.publishPaymentFailed("{\"error\":\"failed\"}", "1")
        );

        verify(kafkaTemplate, times(1)).send(eq("payment-failed"), eq("1"), any(String.class));
    }

    @Test
    void publishPaymentFailed_differentOrderId() throws Exception {
        when(kafkaTemplate.send(eq("payment-failed"), eq("3"), any(String.class)))
                .thenReturn(future);
        when(future.get()).thenReturn(mock(SendResult.class));

        assertDoesNotThrow(() -> 
            paymentEventProducer.publishPaymentFailed("{\"error\":\"failed\"}", "3")
        );

        verify(kafkaTemplate, times(1)).send(eq("payment-failed"), eq("3"), any(String.class));
    }

    @Test
    void publishPaymentFailed_exceptionHandling() throws Exception {
        when(kafkaTemplate.send(eq("payment-failed"), eq("1"), any(String.class)))
                .thenReturn(future);
        when(future.get()).thenThrow(new RuntimeException("Kafka error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            paymentEventProducer.publishPaymentFailed("{\"error\":\"failed\"}", "1")
        );

        assertTrue(exception.getMessage().contains("Failed to publish PAYMENT_FAILED event"));
        assertNotNull(exception.getCause());
    }
}
