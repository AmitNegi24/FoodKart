package com.foodkart.payment_service.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private PaymentRequestDTO paymentRequestDTO;
    private PaymentResponseDTO paymentResponseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
        objectMapper = new ObjectMapper();

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
    }

    @Test
    void processPayment_success() {
        when(paymentService.processPayment(any(PaymentRequestDTO.class)))
                .thenReturn(paymentResponseDTO);

        ResponseEntity<PaymentResponseDTO> result = paymentController.processPayment(paymentRequestDTO);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(1L, result.getBody().getId());
        assertEquals("test@example.com", result.getBody().getUserEmailId());
        verify(paymentService, times(1)).processPayment(paymentRequestDTO);
    }

    @Test
    void processPayment_withNullCorrelationId() {
        paymentRequestDTO.setCorrelationId(null);
        when(paymentService.processPayment(any(PaymentRequestDTO.class)))
                .thenReturn(paymentResponseDTO);

        ResponseEntity<PaymentResponseDTO> result = paymentController.processPayment(paymentRequestDTO);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(paymentService, times(1)).processPayment(paymentRequestDTO);
    }

    @Test
    void getPaymentByOrderId_success() {
        when(paymentService.getPaymentByOrderId(1L)).thenReturn(paymentResponseDTO);

        ResponseEntity<PaymentResponseDTO> result = paymentController.getPaymentByOrderId(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1L, result.getBody().getId());
        verify(paymentService, times(1)).getPaymentByOrderId(1L);
    }

    @Test
    void getPaymentByOrderId_notFound_throwsException() {
        when(paymentService.getPaymentByOrderId(999L))
                .thenThrow(new RuntimeException("Payment not found"));

        assertThrows(RuntimeException.class, () -> paymentController.getPaymentByOrderId(999L));
        verify(paymentService, times(1)).getPaymentByOrderId(999L);
    }

    @Test
    void processPayment_withMockMvc_success() throws Exception {
        when(paymentService.processPayment(any(PaymentRequestDTO.class)))
                .thenReturn(paymentResponseDTO);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequestDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void getPaymentByOrderId_withMockMvc_success() throws Exception {
        when(paymentService.getPaymentByOrderId(1L)).thenReturn(paymentResponseDTO);

        mockMvc.perform(get("/payments/order/1"))
                .andExpect(status().isOk());
    }
}
