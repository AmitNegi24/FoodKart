package com.foodkart.order_service.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void testBuilder() {
        Order order = Order.builder()
                .id(1L)
                .userId("user@example.com")
                .userEmail("user@example.com")
                .restaurantId(100L)
                .totalAmount(new BigDecimal("20.00"))
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .items(List.of())
                .build();

        assertEquals(1L, order.getId());
        assertEquals("user@example.com", order.getUserId());
        assertEquals("user@example.com", order.getUserEmail());
        assertEquals(100L, order.getRestaurantId());
        assertEquals(new BigDecimal("20.00"), order.getTotalAmount());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertNotNull(order.getCreatedAt());
        assertNotNull(order.getUpdatedAt());
        assertNotNull(order.getItems());
    }

    @Test
    void testNoArgsConstructor() {
        Order order = new Order();
        assertNotNull(order);
        assertNull(order.getId());
        assertNull(order.getUserId());
        assertNull(order.getUserEmail());
        assertNull(order.getRestaurantId());
        assertNull(order.getTotalAmount());
        assertNull(order.getStatus());
        assertNull(order.getCreatedAt());
        assertNull(order.getUpdatedAt());
        assertNull(order.getItems());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Order order = new Order(
                1L,
                "user@example.com",
                "user@example.com",
                100L,
                new BigDecimal("20.00"),
                OrderStatus.PENDING,
                now,
                now,
                List.of()
        );

        assertEquals(1L, order.getId());
        assertEquals("user@example.com", order.getUserId());
        assertEquals("user@example.com", order.getUserEmail());
        assertEquals(100L, order.getRestaurantId());
        assertEquals(new BigDecimal("20.00"), order.getTotalAmount());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(now, order.getCreatedAt());
        assertEquals(now, order.getUpdatedAt());
        assertNotNull(order.getItems());
    }

    @Test
    void testSetters() {
        Order order = new Order();
        order.setId(1L);
        order.setUserId("user@example.com");
        order.setUserEmail("user@example.com");
        order.setRestaurantId(100L);
        order.setTotalAmount(new BigDecimal("20.00"));
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setItems(List.of());

        assertEquals(1L, order.getId());
        assertEquals("user@example.com", order.getUserId());
        assertEquals("user@example.com", order.getUserEmail());
        assertEquals(100L, order.getRestaurantId());
        assertEquals(new BigDecimal("20.00"), order.getTotalAmount());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertNotNull(order.getCreatedAt());
        assertNotNull(order.getUpdatedAt());
        assertNotNull(order.getItems());
    }

    @Test
    void testPrePersist() {
        Order order = Order.builder()
                .userId("user@example.com")
                .userEmail("user@example.com")
                .restaurantId(100L)
                .totalAmount(new BigDecimal("20.00"))
                .items(List.of())
                .build();

        order.onCreate();

        assertNotNull(order.getCreatedAt());
        assertNotNull(order.getUpdatedAt());
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void testPrePersistWithExistingStatus() {
        Order order = Order.builder()
                .userId("user@example.com")
                .userEmail("user@example.com")
                .restaurantId(100L)
                .totalAmount(new BigDecimal("20.00"))
                .status(OrderStatus.CONFIRMED)
                .items(List.of())
                .build();

        order.onCreate();

        assertNotNull(order.getCreatedAt());
        assertNotNull(order.getUpdatedAt());
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    void testPreUpdate() {
        Order order = Order.builder()
                .userId("user@example.com")
                .userEmail("user@example.com")
                .restaurantId(100L)
                .totalAmount(new BigDecimal("20.00"))
                .status(OrderStatus.PENDING)
                .items(List.of())
                .build();

        order.onCreate();
        LocalDateTime originalUpdatedAt = order.getUpdatedAt();

        order.onUpdate();

        assertNotNull(order.getUpdatedAt());
        assertTrue(order.getUpdatedAt().isAfter(originalUpdatedAt) || order.getUpdatedAt().isEqual(originalUpdatedAt));
    }
}
