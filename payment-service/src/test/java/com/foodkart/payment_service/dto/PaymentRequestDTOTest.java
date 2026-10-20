package com.foodkart.payment_service.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PaymentRequestDTOTest {

    @Test
    void testBuilder() {
        PaymentRequestDTO dto = PaymentRequestDTO.builder()
                .orderId(1L)
                .userEmailId("test@example.com")
                .amount(new BigDecimal("100.00"))
                .correlationId("corr-123")
                .build();

        assertEquals(1L, dto.getOrderId());
        assertEquals("test@example.com", dto.getUserEmailId());
        assertEquals(new BigDecimal("100.00"), dto.getAmount());
        assertEquals("corr-123", dto.getCorrelationId());
    }

    @Test
    void testSettersAndGetters() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setOrderId(2L);
        dto.setUserEmailId("user@test.com");
        dto.setAmount(new BigDecimal("200.00"));
        dto.setCorrelationId("corr-456");

        assertEquals(2L, dto.getOrderId());
        assertEquals("user@test.com", dto.getUserEmailId());
        assertEquals(new BigDecimal("200.00"), dto.getAmount());
        assertEquals("corr-456", dto.getCorrelationId());
    }

    @Test
    void testNoArgsConstructor() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        assertNotNull(dto);
        assertNull(dto.getOrderId());
        assertNull(dto.getUserEmailId());
        assertNull(dto.getAmount());
        assertNull(dto.getCorrelationId());
    }

    @Test
    void testAllArgsConstructor() {
        PaymentRequestDTO dto = new PaymentRequestDTO(3L, "all@test.com", new BigDecimal("300.00"), "corr-789");

        assertEquals(3L, dto.getOrderId());
        assertEquals("all@test.com", dto.getUserEmailId());
        assertEquals(new BigDecimal("300.00"), dto.getAmount());
        assertEquals("corr-789", dto.getCorrelationId());
    }

    @Test
    void testToString() {
        PaymentRequestDTO dto = PaymentRequestDTO.builder()
                .orderId(1L)
                .userEmailId("test@example.com")
                .amount(new BigDecimal("100.00"))
                .build();

        String toString = dto.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("PaymentRequestDTO"));
    }
}
