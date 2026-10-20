package com.foodkart.delivery_service.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryPartnerTest {

    @Test
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();
        DeliveryPartner partner = DeliveryPartner.builder()
                .id(1L)
                .name("John Doe")
                .phone("1234567890")
                .available(true)
                .createdAt(now)
                .build();

        assertEquals(1L, partner.getId());
        assertEquals("John Doe", partner.getName());
        assertEquals("1234567890", partner.getPhone());
        assertTrue(partner.isAvailable());
        assertEquals(now, partner.getCreatedAt());
    }

    @Test
    void testSettersAndGetters() {
        DeliveryPartner partner = new DeliveryPartner();
        LocalDateTime now = LocalDateTime.now();

        partner.setId(2L);
        partner.setName("Jane Smith");
        partner.setPhone("9876543210");
        partner.setAvailable(false);
        partner.setCreatedAt(now);

        assertEquals(2L, partner.getId());
        assertEquals("Jane Smith", partner.getName());
        assertEquals("9876543210", partner.getPhone());
        assertFalse(partner.isAvailable());
        assertEquals(now, partner.getCreatedAt());
    }

    @Test
    void testNoArgsConstructor() {
        DeliveryPartner partner = new DeliveryPartner();
        assertNotNull(partner);
        assertNull(partner.getId());
        assertNull(partner.getName());
        assertNull(partner.getPhone());
        assertFalse(partner.isAvailable());
        assertNull(partner.getCreatedAt());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        DeliveryPartner partner = new DeliveryPartner(
                3L, "Bob Johnson", "5555555555", true, now
        );

        assertEquals(3L, partner.getId());
        assertEquals("Bob Johnson", partner.getName());
        assertEquals("5555555555", partner.getPhone());
        assertTrue(partner.isAvailable());
        assertEquals(now, partner.getCreatedAt());
    }

    @Test
    void testOnCreate() {
        DeliveryPartner partner = new DeliveryPartner();
        partner.onCreate();

        assertNotNull(partner.getCreatedAt());
    }
}
