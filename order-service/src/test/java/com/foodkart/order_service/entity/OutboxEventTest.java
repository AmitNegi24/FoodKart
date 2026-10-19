package com.foodkart.order_service.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OutboxEventTest {

    @Test
    void testBuilder() {
        UUID eventId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .id(1L)
                .eventId(eventId)
                .aggregateType("ORDER")
                .aggregateId("123")
                .eventType("ORDER_CREATED")
                .payload("{\"test\":\"data\"}")
                .status(OutboxStatus.PENDING)
                .createdAt(now)
                .publishedAt(now)
                .build();

        assertEquals(1L, outboxEvent.getId());
        assertEquals(eventId, outboxEvent.getEventId());
        assertEquals("ORDER", outboxEvent.getAggregateType());
        assertEquals("123", outboxEvent.getAggregateId());
        assertEquals("ORDER_CREATED", outboxEvent.getEventType());
        assertEquals("{\"test\":\"data\"}", outboxEvent.getPayload());
        assertEquals(OutboxStatus.PENDING, outboxEvent.getStatus());
        assertEquals(now, outboxEvent.getCreatedAt());
        assertEquals(now, outboxEvent.getPublishedAt());
    }

    @Test
    void testNoArgsConstructor() {
        OutboxEvent outboxEvent = new OutboxEvent();
        assertNotNull(outboxEvent);
        assertNull(outboxEvent.getId());
        assertNull(outboxEvent.getEventId());
        assertNull(outboxEvent.getAggregateType());
        assertNull(outboxEvent.getAggregateId());
        assertNull(outboxEvent.getEventType());
        assertNull(outboxEvent.getPayload());
        assertNull(outboxEvent.getStatus());
        assertNull(outboxEvent.getCreatedAt());
        assertNull(outboxEvent.getPublishedAt());
    }

    @Test
    void testAllArgsConstructor() {
        UUID eventId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        OutboxEvent outboxEvent = new OutboxEvent(
                1L,
                eventId,
                "ORDER",
                "123",
                "ORDER_CREATED",
                "{\"test\":\"data\"}",
                OutboxStatus.PENDING,
                now,
                now
        );

        assertEquals(1L, outboxEvent.getId());
        assertEquals(eventId, outboxEvent.getEventId());
        assertEquals("ORDER", outboxEvent.getAggregateType());
        assertEquals("123", outboxEvent.getAggregateId());
        assertEquals("ORDER_CREATED", outboxEvent.getEventType());
        assertEquals("{\"test\":\"data\"}", outboxEvent.getPayload());
        assertEquals(OutboxStatus.PENDING, outboxEvent.getStatus());
        assertEquals(now, outboxEvent.getCreatedAt());
        assertEquals(now, outboxEvent.getPublishedAt());
    }

    @Test
    void testSetters() {
        UUID eventId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setId(1L);
        outboxEvent.setEventId(eventId);
        outboxEvent.setAggregateType("ORDER");
        outboxEvent.setAggregateId("123");
        outboxEvent.setEventType("ORDER_CREATED");
        outboxEvent.setPayload("{\"test\":\"data\"}");
        outboxEvent.setStatus(OutboxStatus.PENDING);
        outboxEvent.setCreatedAt(now);
        outboxEvent.setPublishedAt(now);

        assertEquals(1L, outboxEvent.getId());
        assertEquals(eventId, outboxEvent.getEventId());
        assertEquals("ORDER", outboxEvent.getAggregateType());
        assertEquals("123", outboxEvent.getAggregateId());
        assertEquals("ORDER_CREATED", outboxEvent.getEventType());
        assertEquals("{\"test\":\"data\"}", outboxEvent.getPayload());
        assertEquals(OutboxStatus.PENDING, outboxEvent.getStatus());
        assertEquals(now, outboxEvent.getCreatedAt());
        assertEquals(now, outboxEvent.getPublishedAt());
    }

    @Test
    void testPrePersist() {
        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateType("ORDER")
                .aggregateId("123")
                .eventType("ORDER_CREATED")
                .payload("{\"test\":\"data\"}")
                .status(OutboxStatus.PENDING)
                .build();

        outboxEvent.onCreate();

        assertNotNull(outboxEvent.getEventId());
        assertNotNull(outboxEvent.getCreatedAt());
    }

    @Test
    void testPrePersistWithExistingEventId() {
        UUID existingEventId = UUID.randomUUID();
        OutboxEvent outboxEvent = OutboxEvent.builder()
                .eventId(existingEventId)
                .aggregateType("ORDER")
                .aggregateId("123")
                .eventType("ORDER_CREATED")
                .payload("{\"test\":\"data\"}")
                .status(OutboxStatus.PENDING)
                .build();

        outboxEvent.onCreate();

        assertEquals(existingEventId, outboxEvent.getEventId());
        assertNotNull(outboxEvent.getCreatedAt());
    }
}
