package com.foodkart.order_service.dto;

import com.foodkart.order_service.entity.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderResponseDTOTest {

    @Test
    void testBuilder() {
        OrderItemResponseDTO item = OrderItemResponseDTO.builder()
                .menuItemId(1L)
                .itemName("Burger")
                .quantity(2)
                .price(new BigDecimal("10.00"))
                .subtotal(new BigDecimal("20.00"))
                .build();

        OrderResponseDTO dto = OrderResponseDTO.builder()
                .id(1L)
                .userEmailId("test@example.com")
                .restaurantId(100L)
                .totalAmount(new BigDecimal("20.00"))
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .items(List.of(item))
                .build();

        assertEquals(1L, dto.getId());
        assertEquals("test@example.com", dto.getUserEmailId());
        assertEquals(100L, dto.getRestaurantId());
        assertEquals(new BigDecimal("20.00"), dto.getTotalAmount());
        assertEquals(OrderStatus.PENDING, dto.getStatus());
        assertNotNull(dto.getCreatedAt());
        assertEquals(1, dto.getItems().size());
    }

    @Test
    void testNoArgsConstructor() {
        OrderResponseDTO dto = new OrderResponseDTO();
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getUserEmailId());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        OrderItemResponseDTO item = new OrderItemResponseDTO(1L, "Burger", 2, new BigDecimal("10.00"), new BigDecimal("20.00"));

        OrderResponseDTO dto = new OrderResponseDTO(
                1L,
                "test@example.com",
                100L,
                new BigDecimal("20.00"),
                OrderStatus.PENDING,
                now,
                List.of(item)
        );

        assertEquals(1L, dto.getId());
        assertEquals("test@example.com", dto.getUserEmailId());
        assertEquals(100L, dto.getRestaurantId());
        assertEquals(new BigDecimal("20.00"), dto.getTotalAmount());
        assertEquals(OrderStatus.PENDING, dto.getStatus());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(1, dto.getItems().size());
    }

    @Test
    void testSetters() {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(1L);
        dto.setUserEmailId("test@example.com");
        dto.setRestaurantId(100L);
        dto.setTotalAmount(new BigDecimal("20.00"));
        dto.setStatus(OrderStatus.PENDING);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setItems(List.of());

        assertEquals(1L, dto.getId());
        assertEquals("test@example.com", dto.getUserEmailId());
        assertEquals(100L, dto.getRestaurantId());
        assertEquals(new BigDecimal("20.00"), dto.getTotalAmount());
        assertEquals(OrderStatus.PENDING, dto.getStatus());
        assertNotNull(dto.getCreatedAt());
        assertNotNull(dto.getItems());
    }
}
