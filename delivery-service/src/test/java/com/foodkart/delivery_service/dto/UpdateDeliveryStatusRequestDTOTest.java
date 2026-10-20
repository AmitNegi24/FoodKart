package com.foodkart.delivery_service.dto;

import com.foodkart.delivery_service.entity.DeliveryStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateDeliveryStatusRequestDTOTest {

    @Test
    void testBuilder() {
        UpdateDeliveryStatusRequestDTO dto = UpdateDeliveryStatusRequestDTO.builder()
                .status(DeliveryStatus.DELIVERED)
                .build();

        assertEquals(DeliveryStatus.DELIVERED, dto.getStatus());
    }

    @Test
    void testSettersAndGetters() {
        UpdateDeliveryStatusRequestDTO dto = new UpdateDeliveryStatusRequestDTO();
        dto.setStatus(DeliveryStatus.PICKED_UP);

        assertEquals(DeliveryStatus.PICKED_UP, dto.getStatus());
    }

    @Test
    void testNoArgsConstructor() {
        UpdateDeliveryStatusRequestDTO dto = new UpdateDeliveryStatusRequestDTO();
        assertNotNull(dto);
        assertNull(dto.getStatus());
    }

    @Test
    void testAllArgsConstructor() {
        UpdateDeliveryStatusRequestDTO dto = new UpdateDeliveryStatusRequestDTO(
                DeliveryStatus.OUT_FOR_DELIVERY
        );

        assertEquals(DeliveryStatus.OUT_FOR_DELIVERY, dto.getStatus());
    }

    @Test
    void testValidationNotNull() {
        UpdateDeliveryStatusRequestDTO dto = new UpdateDeliveryStatusRequestDTO();
        dto.setStatus(null);

        assertNull(dto.getStatus());
    }
}
