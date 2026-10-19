package com.foodkart.order_service.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemRequestDTOTest {

    @Test
    void testGettersAndSetters() {
        OrderItemRequestDTO dto = new OrderItemRequestDTO();
        dto.setMenuItemId(1L);
        dto.setQuantity(2);

        assertEquals(1L, dto.getMenuItemId());
        assertEquals(2, dto.getQuantity());
    }

    @Test
    void testToString() {
        OrderItemRequestDTO dto = new OrderItemRequestDTO();
        dto.setMenuItemId(1L);
        dto.setQuantity(2);

        String result = dto.toString();
        assertTrue(result.contains("menuItemId=1"));
        assertTrue(result.contains("quantity=2"));
    }

    @Test
    void testConstructor() {
        OrderItemRequestDTO dto = new OrderItemRequestDTO();
        assertNotNull(dto);
        assertNull(dto.getMenuItemId());
        assertNull(dto.getQuantity());
    }
}
