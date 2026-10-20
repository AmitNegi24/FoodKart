package com.foodkart.delivery_service.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryPartnerResponseDTOTest {

    @Test
    void testBuilder() {
        DeliveryPartnerResponseDTO dto = DeliveryPartnerResponseDTO.builder()
                .id(1L)
                .name("John Doe")
                .phone("1234567890")
                .available(true)
                .build();

        assertEquals(1L, dto.getId());
        assertEquals("John Doe", dto.getName());
        assertEquals("1234567890", dto.getPhone());
        assertTrue(dto.isAvailable());
    }

    @Test
    void testGetters() {
        DeliveryPartnerResponseDTO dto = DeliveryPartnerResponseDTO.builder()
                .id(2L)
                .name("Jane Smith")
                .phone("9876543210")
                .available(false)
                .build();

        assertEquals(2L, dto.getId());
        assertEquals("Jane Smith", dto.getName());
        assertEquals("9876543210", dto.getPhone());
        assertFalse(dto.isAvailable());
    }

    @Test
    void testBuilderWithNullValues() {
        DeliveryPartnerResponseDTO dto = DeliveryPartnerResponseDTO.builder()
                .build();

        assertNull(dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getPhone());
        assertFalse(dto.isAvailable());
    }
}
