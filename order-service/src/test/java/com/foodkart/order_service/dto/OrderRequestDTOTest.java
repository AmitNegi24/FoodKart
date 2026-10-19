package com.foodkart.order_service.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderRequestDTOTest {

    @Test
    void testGettersAndSetters() {
        OrderRequestDTO dto = new OrderRequestDTO();
        OrderItemRequestDTO item = new OrderItemRequestDTO();
        item.setMenuItemId(1L);
        item.setQuantity(2);

        dto.setRestaurantId(100L);
        dto.setItems(List.of(item));

        assertEquals(100L, dto.getRestaurantId());
        assertEquals(1, dto.getItems().size());
        assertEquals(1L, dto.getItems().get(0).getMenuItemId());
        assertEquals(2, dto.getItems().get(0).getQuantity());
    }

    @Test
    void testToString() {
        OrderRequestDTO dto = new OrderRequestDTO();
        dto.setRestaurantId(100L);
        dto.setItems(List.of());

        String result = dto.toString();
        assertTrue(result.contains("restaurantId=100"));
        assertTrue(result.contains("items="));
    }

    @Test
    void testConstructor() {
        OrderRequestDTO dto = new OrderRequestDTO();
        assertNotNull(dto);
        assertNull(dto.getRestaurantId());
        assertNull(dto.getItems());
    }
}
