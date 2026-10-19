package com.foodkart.order_service.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    void testBuilder() {
        Order order = Order.builder().id(1L).build();

        OrderItem orderItem = OrderItem.builder()
                .id(1L)
                .order(order)
                .menuItemId(10L)
                .itemName("Burger")
                .price(new BigDecimal("10.00"))
                .quantity(2)
                .subtotal(new BigDecimal("20.00"))
                .build();

        assertEquals(1L, orderItem.getId());
        assertEquals(order, orderItem.getOrder());
        assertEquals(10L, orderItem.getMenuItemId());
        assertEquals("Burger", orderItem.getItemName());
        assertEquals(new BigDecimal("10.00"), orderItem.getPrice());
        assertEquals(2, orderItem.getQuantity());
        assertEquals(new BigDecimal("20.00"), orderItem.getSubtotal());
    }

    @Test
    void testNoArgsConstructor() {
        OrderItem orderItem = new OrderItem();
        assertNotNull(orderItem);
        assertNull(orderItem.getId());
        assertNull(orderItem.getOrder());
        assertNull(orderItem.getMenuItemId());
        assertNull(orderItem.getItemName());
        assertNull(orderItem.getPrice());
        assertNull(orderItem.getQuantity());
        assertNull(orderItem.getSubtotal());
    }

    @Test
    void testAllArgsConstructor() {
        Order order = Order.builder().id(1L).build();

        OrderItem orderItem = new OrderItem(
                1L,
                order,
                10L,
                "Burger",
                new BigDecimal("10.00"),
                2,
                new BigDecimal("20.00")
        );

        assertEquals(1L, orderItem.getId());
        assertEquals(order, orderItem.getOrder());
        assertEquals(10L, orderItem.getMenuItemId());
        assertEquals("Burger", orderItem.getItemName());
        assertEquals(new BigDecimal("10.00"), orderItem.getPrice());
        assertEquals(2, orderItem.getQuantity());
        assertEquals(new BigDecimal("20.00"), orderItem.getSubtotal());
    }

    @Test
    void testSetters() {
        Order order = Order.builder().id(1L).build();

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrder(order);
        orderItem.setMenuItemId(10L);
        orderItem.setItemName("Burger");
        orderItem.setPrice(new BigDecimal("10.00"));
        orderItem.setQuantity(2);
        orderItem.setSubtotal(new BigDecimal("20.00"));

        assertEquals(1L, orderItem.getId());
        assertEquals(order, orderItem.getOrder());
        assertEquals(10L, orderItem.getMenuItemId());
        assertEquals("Burger", orderItem.getItemName());
        assertEquals(new BigDecimal("10.00"), orderItem.getPrice());
        assertEquals(2, orderItem.getQuantity());
        assertEquals(new BigDecimal("20.00"), orderItem.getSubtotal());
    }
}
