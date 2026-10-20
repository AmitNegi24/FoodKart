package com.foodkart.payment_service.kafka;

import tools.jackson.databind.ObjectMapper;
import com.foodkart.payment_service.dto.PaymentRequestDTO;
import com.foodkart.payment_service.dto.PaymentResponseDTO;
import com.foodkart.payment_service.entity.PaymentStatus;
import com.foodkart.payment_service.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentConsumerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private PaymentService paymentService;

    private PaymentConsumer paymentConsumer;

    private PaymentRequestDTO paymentRequestDTO;
    private PaymentResponseDTO paymentResponseDTO;

    @BeforeEach
    void setUp() {
        paymentConsumer = new PaymentConsumer(objectMapper, paymentService);

        paymentRequestDTO = PaymentRequestDTO.builder()
                .orderId(1L)
                .userEmailId("test@example.com")
                .amount(new BigDecimal("100.00"))
                .correlationId("corr-123")
                .build();

        paymentResponseDTO = PaymentResponseDTO.builder()
                .id(1L)
                .orderId(1L)
                .userEmailId("test@example.com")
                .amount(new BigDecimal("100.00"))
                .status(PaymentStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();

        lenient().when(objectMapper.readValue(anyString(), eq(PaymentRequestDTO.class)))
                .thenReturn(paymentRequestDTO);
        lenient().when(paymentService.processPayment(any(PaymentRequestDTO.class)))
                .thenReturn(paymentResponseDTO);
    }

    @Test
    void consumeOrderCreated_success() {
        assertDoesNotThrow(() -> 
            paymentConsumer.consumeOrderCreated("{\"orderId\":1}")
        );

        verify(objectMapper, times(1)).readValue(anyString(), eq(PaymentRequestDTO.class));
        verify(paymentService, times(1)).processPayment(any(PaymentRequestDTO.class));
    }

    @Test
    void consumeOrderCreated_exceptionHandling() {
        when(objectMapper.readValue(anyString(), eq(PaymentRequestDTO.class)))
                .thenThrow(new RuntimeException("Deserialization error"));

        assertDoesNotThrow(() -> 
            paymentConsumer.consumeOrderCreated("{\"orderId\":1}")
        );

        verify(objectMapper, times(1)).readValue(anyString(), eq(PaymentRequestDTO.class));
        verify(paymentService, never()).processPayment(any(PaymentRequestDTO.class));
    }

    @Test
    void consumeOrderCreated_paymentServiceException() {
        when(paymentService.processPayment(any(PaymentRequestDTO.class)))
                .thenThrow(new RuntimeException("Payment processing error"));

        assertDoesNotThrow(() -> 
            paymentConsumer.consumeOrderCreated("{\"orderId\":1}")
        );

        verify(objectMapper, times(1)).readValue(anyString(), eq(PaymentRequestDTO.class));
        verify(paymentService, times(1)).processPayment(any(PaymentRequestDTO.class));
    }

    @Test
    void consumeOrderCreated_differentOrder() {
        paymentRequestDTO.setOrderId(2L);
        paymentResponseDTO.setOrderId(2L);

        assertDoesNotThrow(() -> 
            paymentConsumer.consumeOrderCreated("{\"orderId\":2}")
        );

        verify(objectMapper, times(1)).readValue(anyString(), eq(PaymentRequestDTO.class));
        verify(paymentService, times(1)).processPayment(any(PaymentRequestDTO.class));
    }
}
