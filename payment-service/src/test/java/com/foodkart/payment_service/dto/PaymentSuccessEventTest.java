package com.foodkart.payment_service.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PaymentSuccessEventTest {

    @Test
    void testBuilder() {
        PaymentSuccessEvent event = PaymentSuccessEvent.builder()
                .paymentId(1L)
                .orderId(100L)
                .userEmailId("test@example.com")
                .amount(new BigDecimal("100.00"))
                .correlationId("corr-123")
                .build();

        assertEquals(1L, event.getPaymentId());
        assertEquals(100L, event.getOrderId());
        assertEquals("test@example.com", event.getUserEmailId());
        assertEquals(new BigDecimal("100.00"), event.getAmount());
        assertEquals("corr-123", event.getCorrelationId());
    }

    @Test
    void testSettersAndGetters() {
        PaymentSuccessEvent event = new PaymentSuccessEvent();
        event.setPaymentId(2L);
        event.setOrderId(200L);
        event.setUserEmailId("user@test.com");
        event.setAmount(new BigDecimal("200.00"));
        event.setCorrelationId("corr-456");

        assertEquals(2L, event.getPaymentId());
        assertEquals(200L, event.getOrderId());
        assertEquals("user@test.com", event.getUserEmailId());
        assertEquals(new BigDecimal("200.00"), event.getAmount());
        assertEquals("corr-456", event.getCorrelationId());
    }

    @Test
    void testNoArgsConstructor() {
        PaymentSuccessEvent event = new PaymentSuccessEvent();
        assertNotNull(event);
        assertNull(event.getPaymentId());
        assertNull(event.getOrderId());
        assertNull(event.getUserEmailId());
        assertNull(event.getAmount());
        assertNull(event.getCorrelationId());
    }

    @Test
    void testAllArgsConstructor() {
        PaymentSuccessEvent event = new PaymentSuccessEvent(3L, 300L, "all@test.com", new BigDecimal("300.00"), "corr-789");

        assertEquals(3L, event.getPaymentId());
        assertEquals(300L, event.getOrderId());
        assertEquals("all@test.com", event.getUserEmailId());
        assertEquals(new BigDecimal("300.00"), event.getAmount());
        assertEquals("corr-789", event.getCorrelationId());
    }
}
