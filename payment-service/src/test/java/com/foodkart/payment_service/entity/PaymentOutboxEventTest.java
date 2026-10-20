package com.foodkart.payment_service.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PaymentOutboxEventTest {

    @Test
    void testBuilder() {
        PaymentOutboxEvent event = PaymentOutboxEvent.builder()
                .id(1L)
                .aggregateId(100L)
                .eventType("PAYMENT_SUCCESS")
                .payload("{\"test\":\"data\"}")
                .status(OutboxStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .processedAt(LocalDateTime.now())
                .build();

        assertEquals(1L, event.getId());
        assertEquals(100L, event.getAggregateId());
        assertEquals("PAYMENT_SUCCESS", event.getEventType());
        assertEquals("{\"test\":\"data\"}", event.getPayload());
        assertEquals(OutboxStatus.PENDING, event.getStatus());
        assertNotNull(event.getCreatedAt());
        assertNotNull(event.getProcessedAt());
    }

    @Test
    void testSettersAndGetters() {
        PaymentOutboxEvent event = new PaymentOutboxEvent();
        event.setId(2L);
        event.setAggregateId(200L);
        event.setEventType("PAYMENT_FAILED");
        event.setPayload("{\"error\":\"failed\"}");
        event.setStatus(OutboxStatus.PROCESSED);
        event.setCreatedAt(LocalDateTime.now());
        event.setProcessedAt(LocalDateTime.now());

        assertEquals(2L, event.getId());
        assertEquals(200L, event.getAggregateId());
        assertEquals("PAYMENT_FAILED", event.getEventType());
        assertEquals("{\"error\":\"failed\"}", event.getPayload());
        assertEquals(OutboxStatus.PROCESSED, event.getStatus());
        assertNotNull(event.getCreatedAt());
        assertNotNull(event.getProcessedAt());
    }

    @Test
    void testNoArgsConstructor() {
        PaymentOutboxEvent event = new PaymentOutboxEvent();
        assertNotNull(event);
        assertNull(event.getId());
        assertNull(event.getAggregateId());
        assertNull(event.getEventType());
        assertNull(event.getPayload());
        assertNull(event.getStatus());
        assertNull(event.getCreatedAt());
        assertNull(event.getProcessedAt());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        PaymentOutboxEvent event = new PaymentOutboxEvent(3L, 300L, "PAYMENT_SUCCESS", "{\"test\":\"data\"}", OutboxStatus.PROCESSED, now, now);

        assertEquals(3L, event.getId());
        assertEquals(300L, event.getAggregateId());
        assertEquals("PAYMENT_SUCCESS", event.getEventType());
        assertEquals("{\"test\":\"data\"}", event.getPayload());
        assertEquals(OutboxStatus.PROCESSED, event.getStatus());
        assertEquals(now, event.getCreatedAt());
        assertEquals(now, event.getProcessedAt());
    }

    @Test
    void testOnCreateLifecycle() {
        PaymentOutboxEvent event = new PaymentOutboxEvent();
        event.onCreate();

        assertNotNull(event.getCreatedAt());
    }
}
