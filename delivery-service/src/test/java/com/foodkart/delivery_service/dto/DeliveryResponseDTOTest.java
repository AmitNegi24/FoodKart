package com.foodkart.delivery_service.dto;

import com.foodkart.delivery_service.entity.DeliveryStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryResponseDTOTest {

    @Test
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();
        DeliveryResponseDTO dto = DeliveryResponseDTO.builder()
                .deliveryId(1L)
                .orderId(101L)
                .status(DeliveryStatus.ASSIGNED)
                .deliveryPartnerId(10L)
                .deliveryPartnerName("John Doe")
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(1L, dto.getDeliveryId());
        assertEquals(101L, dto.getOrderId());
        assertEquals(DeliveryStatus.ASSIGNED, dto.getStatus());
        assertEquals(10L, dto.getDeliveryPartnerId());
        assertEquals("John Doe", dto.getDeliveryPartnerName());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    void testSettersAndGetters() {
        DeliveryResponseDTO dto = new DeliveryResponseDTO();
        LocalDateTime now = LocalDateTime.now();

        dto.setDeliveryId(2L);
        dto.setOrderId(102L);
        dto.setStatus(DeliveryStatus.PICKED_UP);
        dto.setDeliveryPartnerId(20L);
        dto.setDeliveryPartnerName("Jane Smith");
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);

        assertEquals(2L, dto.getDeliveryId());
        assertEquals(102L, dto.getOrderId());
        assertEquals(DeliveryStatus.PICKED_UP, dto.getStatus());
        assertEquals(20L, dto.getDeliveryPartnerId());
        assertEquals("Jane Smith", dto.getDeliveryPartnerName());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    void testNoArgsConstructor() {
        DeliveryResponseDTO dto = new DeliveryResponseDTO();
        assertNotNull(dto);
        assertNull(dto.getDeliveryId());
        assertNull(dto.getOrderId());
        assertNull(dto.getStatus());
        assertNull(dto.getDeliveryPartnerId());
        assertNull(dto.getDeliveryPartnerName());
        assertNull(dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        DeliveryResponseDTO dto = new DeliveryResponseDTO(
                3L, 103L, DeliveryStatus.OUT_FOR_DELIVERY,
                30L, "Bob Johnson", now, now
        );

        assertEquals(3L, dto.getDeliveryId());
        assertEquals(103L, dto.getOrderId());
        assertEquals(DeliveryStatus.OUT_FOR_DELIVERY, dto.getStatus());
        assertEquals(30L, dto.getDeliveryPartnerId());
        assertEquals("Bob Johnson", dto.getDeliveryPartnerName());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }
}
