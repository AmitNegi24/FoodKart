package com.foodkart.payment_service.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    @Test
    void testBuilder() {
        Payment payment = Payment.builder()
                .id(1L)
                .orderId(100L)
                .userEmailId("test@example.com")
                .amount(new BigDecimal("100.00"))
                .status(PaymentStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        assertEquals(1L, payment.getId());
        assertEquals(100L, payment.getOrderId());
        assertEquals("test@example.com", payment.getUserEmailId());
        assertEquals(new BigDecimal("100.00"), payment.getAmount());
        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
        assertNotNull(payment.getCreatedAt());
        assertNotNull(payment.getUpdatedAt());
    }

    @Test
    void testSettersAndGetters() {
        Payment payment = new Payment();
        payment.setId(2L);
        payment.setOrderId(200L);
        payment.setUserEmailId("user@test.com");
        payment.setAmount(new BigDecimal("200.00"));
        payment.setStatus(PaymentStatus.FAILED);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        assertEquals(2L, payment.getId());
        assertEquals(200L, payment.getOrderId());
        assertEquals("user@test.com", payment.getUserEmailId());
        assertEquals(new BigDecimal("200.00"), payment.getAmount());
        assertEquals(PaymentStatus.FAILED, payment.getStatus());
        assertNotNull(payment.getCreatedAt());
        assertNotNull(payment.getUpdatedAt());
    }

    @Test
    void testNoArgsConstructor() {
        Payment payment = new Payment();
        assertNotNull(payment);
        assertNull(payment.getId());
        assertNull(payment.getOrderId());
        assertNull(payment.getUserEmailId());
        assertNull(payment.getAmount());
        assertNull(payment.getStatus());
        assertNull(payment.getCreatedAt());
        assertNull(payment.getUpdatedAt());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Payment payment = new Payment(3L, 300L, "all@test.com", new BigDecimal("300.00"), PaymentStatus.PENDING, now, now);

        assertEquals(3L, payment.getId());
        assertEquals(300L, payment.getOrderId());
        assertEquals("all@test.com", payment.getUserEmailId());
        assertEquals(new BigDecimal("300.00"), payment.getAmount());
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        assertEquals(now, payment.getCreatedAt());
        assertEquals(now, payment.getUpdatedAt());
    }

    @Test
    void testOnCreateLifecycle() {
        Payment payment = new Payment();
        payment.onCreate();

        assertNotNull(payment.getCreatedAt());
        assertNotNull(payment.getUpdatedAt());
    }

    @Test
    void testOnUpdateLifecycle() {
        Payment payment = new Payment();
        payment.setUpdatedAt(LocalDateTime.now().minusMinutes(1));
        LocalDateTime oldUpdatedAt = payment.getUpdatedAt();
        
        payment.onUpdate();

        assertNotNull(payment.getUpdatedAt());
        assertTrue(payment.getUpdatedAt().isAfter(oldUpdatedAt) || payment.getUpdatedAt().isEqual(oldUpdatedAt));
    }
}
