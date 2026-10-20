package com.foodkart.payment_service.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentFailedEventTest {

    @Test
    void testBuilder() {
        PaymentFailedEvent event = PaymentFailedEvent.builder()
                .orderId(100L)
                .userEmailId("test@example.com")
                .reason("Insufficient funds")
                .correlationId("corr-123")
                .build();

        assertEquals(100L, event.getOrderId());
        assertEquals("test@example.com", event.getUserEmailId());
        assertEquals("Insufficient funds", event.getReason());
        assertEquals("corr-123", event.getCorrelationId());
    }

    @Test
    void testSettersAndGetters() {
        PaymentFailedEvent event = new PaymentFailedEvent();
        event.setOrderId(200L);
        event.setUserEmailId("user@test.com");
        event.setReason("Card declined");
        event.setCorrelationId("corr-456");

        assertEquals(200L, event.getOrderId());
        assertEquals("user@test.com", event.getUserEmailId());
        assertEquals("Card declined", event.getReason());
        assertEquals("corr-456", event.getCorrelationId());
    }

    @Test
    void testNoArgsConstructor() {
        PaymentFailedEvent event = new PaymentFailedEvent();
        assertNotNull(event);
        assertNull(event.getOrderId());
        assertNull(event.getUserEmailId());
        assertNull(event.getReason());
        assertNull(event.getCorrelationId());
    }

    @Test
    void testAllArgsConstructor() {
        PaymentFailedEvent event = new PaymentFailedEvent(300L, "all@test.com", "Timeout", "corr-789");

        assertEquals(300L, event.getOrderId());
        assertEquals("all@test.com", event.getUserEmailId());
        assertEquals("Timeout", event.getReason());
        assertEquals("corr-789", event.getCorrelationId());
    }
}
