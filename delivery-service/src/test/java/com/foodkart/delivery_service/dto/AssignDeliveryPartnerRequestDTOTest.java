package com.foodkart.delivery_service.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AssignDeliveryPartnerRequestDTOTest {

    @Test
    void testSettersAndGetters() {
        AssignDeliveryPartnerRequestDTO dto = new AssignDeliveryPartnerRequestDTO();
        dto.setDeliveryPartnerId(10L);

        assertEquals(10L, dto.getDeliveryPartnerId());
    }

    @Test
    void testNoArgsConstructor() {
        AssignDeliveryPartnerRequestDTO dto = new AssignDeliveryPartnerRequestDTO();
        assertNotNull(dto);
        assertNull(dto.getDeliveryPartnerId());
    }

    @Test
    void testValidationNotNull() {
        AssignDeliveryPartnerRequestDTO dto = new AssignDeliveryPartnerRequestDTO();
        dto.setDeliveryPartnerId(null);

        assertNull(dto.getDeliveryPartnerId());
    }
}
