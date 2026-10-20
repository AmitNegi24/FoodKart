package com.foodkart.delivery_service.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryTest {

    @Test
    void testBuilder() {
        DeliveryPartner partner = DeliveryPartner.builder()
                .id(10L)
                .name("John Doe")
                .phone("1234567890")
                .available(true)
                .build();

        LocalDateTime now = LocalDateTime.now();
        Delivery delivery = Delivery.builder()
                .id(1L)
                .orderId(101L)
                .status(DeliveryStatus.ASSIGNED)
                .deliveryPartner(partner)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(1L, delivery.getId());
        assertEquals(101L, delivery.getOrderId());
        assertEquals(DeliveryStatus.ASSIGNED, delivery.getStatus());
        assertEquals(partner, delivery.getDeliveryPartner());
        assertEquals(now, delivery.getCreatedAt());
        assertEquals(now, delivery.getUpdatedAt());
    }

    @Test
    void testSettersAndGetters() {
        Delivery delivery = new Delivery();
        DeliveryPartner partner = DeliveryPartner.builder()
                .id(20L)
                .name("Jane Smith")
                .phone("9876543210")
                .available(false)
                .build();
        LocalDateTime now = LocalDateTime.now();

        delivery.setId(2L);
        delivery.setOrderId(102L);
        delivery.setStatus(DeliveryStatus.PICKED_UP);
        delivery.setDeliveryPartner(partner);
        delivery.setCreatedAt(now);
        delivery.setUpdatedAt(now);

        assertEquals(2L, delivery.getId());
        assertEquals(102L, delivery.getOrderId());
        assertEquals(DeliveryStatus.PICKED_UP, delivery.getStatus());
        assertEquals(partner, delivery.getDeliveryPartner());
        assertEquals(now, delivery.getCreatedAt());
        assertEquals(now, delivery.getUpdatedAt());
    }

    @Test
    void testNoArgsConstructor() {
        Delivery delivery = new Delivery();
        assertNotNull(delivery);
        assertNull(delivery.getId());
        assertNull(delivery.getOrderId());
        assertNull(delivery.getStatus());
        assertNull(delivery.getDeliveryPartner());
        assertNull(delivery.getCreatedAt());
        assertNull(delivery.getUpdatedAt());
    }

    @Test
    void testAllArgsConstructor() {
        DeliveryPartner partner = DeliveryPartner.builder()
                .id(30L)
                .name("Bob Johnson")
                .phone("5555555555")
                .available(true)
                .build();
        LocalDateTime now = LocalDateTime.now();
        Delivery delivery = new Delivery(
                3L, 103L, DeliveryStatus.OUT_FOR_DELIVERY,
                partner, now, now
        );

        assertEquals(3L, delivery.getId());
        assertEquals(103L, delivery.getOrderId());
        assertEquals(DeliveryStatus.OUT_FOR_DELIVERY, delivery.getStatus());
        assertEquals(partner, delivery.getDeliveryPartner());
        assertEquals(now, delivery.getCreatedAt());
        assertEquals(now, delivery.getUpdatedAt());
    }

    @Test
    void testOnCreate() {
        Delivery delivery = new Delivery();
        delivery.onCreate();

        assertNotNull(delivery.getCreatedAt());
        assertNotNull(delivery.getUpdatedAt());
        assertEquals(delivery.getCreatedAt(), delivery.getUpdatedAt());
    }

    @Test
    void testOnUpdate() {
        Delivery delivery = new Delivery();
        delivery.onCreate();
        
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        delivery.onUpdate();

        assertNotNull(delivery.getUpdatedAt());
        assertTrue(delivery.getUpdatedAt().isAfter(delivery.getCreatedAt()));
    }
}
