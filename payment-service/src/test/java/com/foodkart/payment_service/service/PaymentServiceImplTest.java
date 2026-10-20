
package com.foodkart.payment_service.service;

import com.foodkart.payment_service.dto.*;
import com.foodkart.payment_service.entity.*;
import com.foodkart.payment_service.repository.PaymentOutboxEventRepository;
import com.foodkart.payment_service.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentOutboxEventRepository paymentOutboxEventRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private PaymentRequestDTO request;
    private Payment payment;

    @BeforeEach
    void setUp() {
        request = PaymentRequestDTO.builder()
                .orderId(101L)
                .userEmailId("test@example.com")
                .amount(new BigDecimal("1000.00"))
                .correlationId("corr-101")
                .build();

        payment = Payment.builder()
                .id(1L)
                .orderId(101L)
                .userEmailId("test@example.com")
                .amount(new BigDecimal("1000.00"))
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private void mockNewPayment() {
        when(paymentRepository.findByOrderId(101L))
                .thenReturn(Optional.empty());

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> {
                    Payment p = invocation.getArgument(0);
                    if (p.getId() == null) {
                        p.setId(1L);
                    }
                    return p;
                });
    }

    @Test
    void processPayment_success() {
        mockNewPayment();

        when(objectMapper.writeValueAsString(any()))
                .thenReturn("{\"success\":true}");

        PaymentResponseDTO response =
                paymentService.processPayment(request);

        assertEquals(PaymentStatus.SUCCESS, response.getStatus());
        assertEquals(101L, response.getOrderId());
        assertEquals(1L, response.getId());
        assertEquals("test@example.com", response.getUserEmailId());
        assertEquals(new BigDecimal("1000.00"), response.getAmount());

        verify(paymentRepository, times(2))
                .save(any(Payment.class));

        ArgumentCaptor<PaymentOutboxEvent> captor =
                ArgumentCaptor.forClass(PaymentOutboxEvent.class);

        verify(paymentOutboxEventRepository).save(captor.capture());

        PaymentOutboxEvent event = captor.getValue();

        assertEquals("PAYMENT_SUCCESS", event.getEventType());
        assertEquals(101L, event.getAggregateId());
        assertEquals(OutboxStatus.PENDING, event.getStatus());
        assertEquals("{\"success\":true}", event.getPayload());
    }

    @Test
    void processPayment_amountExactly5000_success() {
        request.setAmount(new BigDecimal("5000.00"));
        mockNewPayment();

        when(objectMapper.writeValueAsString(any()))
                .thenReturn("{}");

        PaymentResponseDTO response =
                paymentService.processPayment(request);

        assertEquals(PaymentStatus.SUCCESS, response.getStatus());

        verify(paymentOutboxEventRepository).save(
                argThat(event ->
                        event.getEventType().equals("PAYMENT_SUCCESS"))
        );
    }

    @Test
    void processPayment_amountAbove5000_failed() {
        request.setAmount(new BigDecimal("5000.01"));
        mockNewPayment();

        when(objectMapper.writeValueAsString(any()))
                .thenReturn("{\"failed\":true}");

        PaymentResponseDTO response =
                paymentService.processPayment(request);

        assertEquals(PaymentStatus.FAILED, response.getStatus());

        ArgumentCaptor<PaymentOutboxEvent> captor =
                ArgumentCaptor.forClass(PaymentOutboxEvent.class);

        verify(paymentOutboxEventRepository).save(captor.capture());

        assertEquals("PAYMENT_FAILED", captor.getValue().getEventType());
        assertEquals(OutboxStatus.PENDING, captor.getValue().getStatus());
    }

    @Test
    void processPayment_existingPayment_returnsExisting() {
        payment.setStatus(PaymentStatus.SUCCESS);

        when(paymentRepository.findByOrderId(101L))
                .thenReturn(Optional.of(payment));

        PaymentResponseDTO response =
                paymentService.processPayment(request);

        assertEquals(PaymentStatus.SUCCESS, response.getStatus());
        assertEquals(1L, response.getId());

        verify(paymentRepository, never()).save(any());
        verifyNoInteractions(paymentOutboxEventRepository, objectMapper);
    }

    @Test
    void processPayment_existingFailedPayment_returnsExisting() {
        payment.setStatus(PaymentStatus.FAILED);

        when(paymentRepository.findByOrderId(101L))
                .thenReturn(Optional.of(payment));

        PaymentResponseDTO response =
                paymentService.processPayment(request);

        assertEquals(PaymentStatus.FAILED, response.getStatus());

        verify(paymentRepository, never()).save(any());
        verifyNoInteractions(paymentOutboxEventRepository);
    }

    @Test
    void processPayment_serializationFailure_throwsException() {
        mockNewPayment();

        when(objectMapper.writeValueAsString(any()))
                .thenThrow(new RuntimeException("JSON error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> paymentService.processPayment(request)
        );

        assertEquals(
                "Failed to create payment outbox event",
                exception.getMessage()
        );

        assertNotNull(exception.getCause());

        verify(paymentOutboxEventRepository, never()).save(any());
    }

    @Test
    void processPayment_outboxRepositoryFailure_throwsException() {
        mockNewPayment();

        when(objectMapper.writeValueAsString(any()))
                .thenReturn("{}");

        when(paymentOutboxEventRepository.save(any()))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> paymentService.processPayment(request)
        );

        assertEquals(
                "Failed to create payment outbox event",
                exception.getMessage()
        );
    }

    @Test
    void getPaymentByOrderId_found() {
        payment.setStatus(PaymentStatus.SUCCESS);

        when(paymentRepository.findByOrderId(101L))
                .thenReturn(Optional.of(payment));

        PaymentResponseDTO response =
                paymentService.getPaymentByOrderId(101L);

        assertEquals(1L, response.getId());
        assertEquals(101L, response.getOrderId());
        assertEquals(PaymentStatus.SUCCESS, response.getStatus());
        assertEquals(payment.getCreatedAt(), response.getCreatedAt());
    }

    @Test
    void getPaymentByOrderId_notFound() {
        when(paymentRepository.findByOrderId(999L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> paymentService.getPaymentByOrderId(999L)
        );

        assertEquals(
                "Payment not found for order: 999",
                exception.getMessage()
        );
    }

    @Test
    void processPayment_successEvent_containsCorrectData() {
        mockNewPayment();

        when(objectMapper.writeValueAsString(
                any(PaymentSuccessEvent.class)))
                .thenAnswer(invocation -> {
                    PaymentSuccessEvent event =
                            invocation.getArgument(0);

                    assertEquals(1L, event.getPaymentId());
                    assertEquals(101L, event.getOrderId());
                    assertEquals("test@example.com", event.getUserEmailId());
                    assertEquals(new BigDecimal("1000.00"), event.getAmount());
                    assertEquals("corr-101", event.getCorrelationId());

                    return "{}";
                });

        paymentService.processPayment(request);

        verify(objectMapper).writeValueAsString(
                any(PaymentSuccessEvent.class));
    }

    @Test
    void processPayment_failedEvent_containsCorrectData() {
        request.setAmount(new BigDecimal("6000"));
        mockNewPayment();

        when(objectMapper.writeValueAsString(
                any(PaymentFailedEvent.class)))
                .thenAnswer(invocation -> {
                    PaymentFailedEvent event =
                            invocation.getArgument(0);

                    assertEquals(101L, event.getOrderId());
                    assertEquals("test@example.com", event.getUserEmailId());
                    assertEquals(
                            "Payment amount exceeds limit",
                            event.getReason());
                    assertEquals("corr-101", event.getCorrelationId());

                    return "{}";
                });

        paymentService.processPayment(request);

        verify(objectMapper).writeValueAsString(
                any(PaymentFailedEvent.class));
    }
}
