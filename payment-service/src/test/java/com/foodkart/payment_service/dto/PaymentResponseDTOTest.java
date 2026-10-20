package com.foodkart.payment_service.dto;

import com.foodkart.payment_service.entity.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PaymentResponseDTOTest {

    @Test
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();
        PaymentResponseDTO dto = PaymentResponseDTO.builder()
                .id(1L)
                .orderId(100L)
                .userEmailId("test@example.com")
                .amount(new BigDecimal("100.00"))
                .status(PaymentStatus.SUCCESS)
                .createdAt(now)
                .build();

        assertEquals(1L, dto.getId());
        assertEquals(100L, dto.getOrderId());
        assertEquals("test@example.com", dto.getUserEmailId());
        assertEquals(new BigDecimal("100.00"), dto.getAmount());
        assertEquals(PaymentStatus.SUCCESS, dto.getStatus());
        assertEquals(now, dto.getCreatedAt());
    }

    @Test
    void testSettersAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setId(2L);
        dto.setOrderId(200L);
        dto.setUserEmailId("user@test.com");
        dto.setAmount(new BigDecimal("200.00"));
        dto.setStatus(PaymentStatus.FAILED);
        dto.setCreatedAt(now);

        assertEquals(2L, dto.getId());
        assertEquals(200L, dto.getOrderId());
        assertEquals("user@test.com", dto.getUserEmailId());
        assertEquals(new BigDecimal("200.00"), dto.getAmount());
        assertEquals(PaymentStatus.FAILED, dto.getStatus());
        assertEquals(now, dto.getCreatedAt());
    }

    @Test
    void testNoArgsConstructor() {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getOrderId());
        assertNull(dto.getUserEmailId());
        assertNull(dto.getAmount());
        assertNull(dto.getStatus());
        assertNull(dto.getCreatedAt());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        PaymentResponseDTO dto = new PaymentResponseDTO(3L, 300L, "all@test.com", new BigDecimal("300.00"), PaymentStatus.PENDING, now);

        assertEquals(3L, dto.getId());
        assertEquals(300L, dto.getOrderId());
        assertEquals("all@test.com", dto.getUserEmailId());
        assertEquals(new BigDecimal("300.00"), dto.getAmount());
        assertEquals(PaymentStatus.PENDING, dto.getStatus());
        assertEquals(now, dto.getCreatedAt());
    }
}
