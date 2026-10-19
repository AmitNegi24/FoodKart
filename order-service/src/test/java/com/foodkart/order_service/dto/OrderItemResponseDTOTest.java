package com.foodkart.order_service.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemResponseDTOTest {

    @Test
    void testBuilder() {
        OrderItemResponseDTO dto = OrderItemResponseDTO.builder()
                .menuItemId(1L)
                .itemName("Burger")
                .quantity(2)
                .price(new BigDecimal("10.00"))
                .subtotal(new BigDecimal("20.00"))
                .build();

        assertEquals(1L, dto.getMenuItemId());
        assertEquals("Burger", dto.getItemName());
        assertEquals(2, dto.getQuantity());
        assertEquals(new BigDecimal("10.00"), dto.getPrice());
        assertEquals(new BigDecimal("20.00"), dto.getSubtotal());
    }

    @Test
    void testNoArgsConstructor() {
        OrderItemResponseDTO dto = new OrderItemResponseDTO();
        assertNotNull(dto);
        assertNull(dto.getMenuItemId());
        assertNull(dto.getItemName());
        assertNull(dto.getQuantity());
        assertNull(dto.getPrice());
        assertNull(dto.getSubtotal());
    }

    @Test
    void testAllArgsConstructor() {
        OrderItemResponseDTO dto = new OrderItemResponseDTO(
                1L,
                "Burger",
                2,
                new BigDecimal("10.00"),
                new BigDecimal("20.00")
        );

        assertEquals(1L, dto.getMenuItemId());
        assertEquals("Burger", dto.getItemName());
        assertEquals(2, dto.getQuantity());
        assertEquals(new BigDecimal("10.00"), dto.getPrice());
        assertEquals(new BigDecimal("20.00"), dto.getSubtotal());
    }

    @Test
    void testSetters() {
        OrderItemResponseDTO dto = new OrderItemResponseDTO();
        dto.setMenuItemId(1L);
        dto.setItemName("Burger");
        dto.setQuantity(2);
        dto.setPrice(new BigDecimal("10.00"));
        dto.setSubtotal(new BigDecimal("20.00"));

        assertEquals(1L, dto.getMenuItemId());
        assertEquals("Burger", dto.getItemName());
        assertEquals(2, dto.getQuantity());
        assertEquals(new BigDecimal("10.00"), dto.getPrice());
        assertEquals(new BigDecimal("20.00"), dto.getSubtotal());
    }
}
