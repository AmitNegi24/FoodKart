package com.foodkart.delivery_service.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateDeliveryPartnerRequestDTOTest {

    @Test
    void testSettersAndGetters() {
        CreateDeliveryPartnerRequestDTO dto = new CreateDeliveryPartnerRequestDTO();
        dto.setName("John Doe");
        dto.setPhone("1234567890");

        assertEquals("John Doe", dto.getName());
        assertEquals("1234567890", dto.getPhone());
    }

    @Test
    void testNoArgsConstructor() {
        CreateDeliveryPartnerRequestDTO dto = new CreateDeliveryPartnerRequestDTO();
        assertNotNull(dto);
        assertNull(dto.getName());
        assertNull(dto.getPhone());
    }

    @Test
    void testValidationAnnotations() {
        CreateDeliveryPartnerRequestDTO dto = new CreateDeliveryPartnerRequestDTO();
        dto.setName("");
        dto.setPhone("");

        assertEquals("", dto.getName());
        assertEquals("", dto.getPhone());
    }
}
