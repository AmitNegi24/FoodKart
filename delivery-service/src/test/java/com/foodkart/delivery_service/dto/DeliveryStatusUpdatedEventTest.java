package com.foodkart.delivery_service.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryStatusUpdatedEventTest {

    @Test
    void testNoArgsConstructor() {
        DeliveryStatusUpdatedEvent event = new DeliveryStatusUpdatedEvent();
        assertNotNull(event);
        assertNull(event.getOrderId());
        assertNull(event.getStatus());
    }

    @Test
    void testAllArgsConstructor() {
        DeliveryStatusUpdatedEvent event = new DeliveryStatusUpdatedEvent(101L, "DELIVERED");

        assertEquals(101L, event.getOrderId());
        assertEquals("DELIVERED", event.getStatus());
    }

    @Test
    void testSettersAndGetters() {
        DeliveryStatusUpdatedEvent event = new DeliveryStatusUpdatedEvent();
        event.setOrderId(202L);
        event.setStatus("PICKED_UP");

        assertEquals(202L, event.getOrderId());
        assertEquals("PICKED_UP", event.getStatus());
    }

    @Test
    void testAllArgsConstructorWithNullValues() {
        DeliveryStatusUpdatedEvent event = new DeliveryStatusUpdatedEvent(null, null);

        assertNull(event.getOrderId());
        assertNull(event.getStatus());
    }
}
