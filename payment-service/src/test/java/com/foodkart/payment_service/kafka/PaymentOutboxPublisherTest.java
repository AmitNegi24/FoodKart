package com.foodkart.payment_service.kafka;

import com.foodkart.payment_service.entity.OutboxStatus;
import com.foodkart.payment_service.entity.PaymentOutboxEvent;
import com.foodkart.payment_service.repository.PaymentOutboxEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentOutboxPublisherTest {

    @Mock
    private PaymentOutboxEventRepository paymentOutboxEventRepository;

    @Mock
    private PaymentEventProducer paymentEventProducer;

    @InjectMocks
    private PaymentOutboxPublisher paymentOutboxPublisher;

    private PaymentOutboxEvent successEvent;
    private PaymentOutboxEvent failedEvent;

    @BeforeEach
    void setUp() {
        successEvent = PaymentOutboxEvent.builder()
                .id(1L)
                .aggregateId(1L)
                .eventType("PAYMENT_SUCCESS")
                .payload("{\"test\":\"data\"}")
                .status(OutboxStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        failedEvent = PaymentOutboxEvent.builder()
                .id(2L)
                .aggregateId(2L)
                .eventType("PAYMENT_FAILED")
                .payload("{\"error\":\"failed\"}")
                .status(OutboxStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void publishPendingEvents_successEvent_success() {
        doNothing().when(paymentEventProducer).publishPaymentSuccess(any(String.class), any(String.class));
        when(paymentOutboxEventRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING))
                .thenReturn(List.of(successEvent));
        when(paymentOutboxEventRepository.save(any(PaymentOutboxEvent.class))).thenReturn(successEvent);

        paymentOutboxPublisher.publishPendingEvents();

        verify(paymentOutboxEventRepository, times(1)).findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        verify(paymentEventProducer, times(1)).publishPaymentSuccess(any(String.class), any(String.class));
        verify(paymentOutboxEventRepository, times(1)).save(any(PaymentOutboxEvent.class));
        assertEquals(OutboxStatus.PROCESSED, successEvent.getStatus());
    }

    @Test
    void publishPendingEvents_failedEvent_success() {
        doNothing().when(paymentEventProducer).publishPaymentFailed(any(String.class), any(String.class));
        when(paymentOutboxEventRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING))
                .thenReturn(List.of(failedEvent));
        when(paymentOutboxEventRepository.save(any(PaymentOutboxEvent.class))).thenReturn(failedEvent);

        paymentOutboxPublisher.publishPendingEvents();

        verify(paymentOutboxEventRepository, times(1)).findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        verify(paymentEventProducer, times(1)).publishPaymentFailed(any(String.class), any(String.class));
        verify(paymentOutboxEventRepository, times(1)).save(any(PaymentOutboxEvent.class));
        assertEquals(OutboxStatus.PROCESSED, failedEvent.getStatus());
    }

    @Test
    void publishPendingEvents_noPendingEvents() {
        when(paymentOutboxEventRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING))
                .thenReturn(Collections.emptyList());

        paymentOutboxPublisher.publishPendingEvents();

        verify(paymentOutboxEventRepository, times(1)).findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        verify(paymentEventProducer, never()).publishPaymentSuccess(any(String.class), any(String.class));
        verify(paymentEventProducer, never()).publishPaymentFailed(any(String.class), any(String.class));
        verify(paymentOutboxEventRepository, never()).save(any(PaymentOutboxEvent.class));
    }

    @Test
    void publishPendingEvents_unknownEventType_throwsException() {
        PaymentOutboxEvent unknownEvent = PaymentOutboxEvent.builder()
                .id(3L)
                .aggregateId(3L)
                .eventType("UNKNOWN_EVENT")
                .payload("{\"test\":\"data\"}")
                .status(OutboxStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(paymentOutboxEventRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING))
                .thenReturn(List.of(unknownEvent));

        paymentOutboxPublisher.publishPendingEvents();

        verify(paymentOutboxEventRepository, times(1)).findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        verify(paymentEventProducer, never()).publishPaymentSuccess(any(String.class), any(String.class));
        verify(paymentEventProducer, never()).publishPaymentFailed(any(String.class), any(String.class));
        verify(paymentOutboxEventRepository, never()).save(any(PaymentOutboxEvent.class));
    }

    @Test
    void publishPendingEvents_exceptionHandling() {
        doThrow(new RuntimeException("Publish error")).when(paymentEventProducer)
                .publishPaymentSuccess(any(String.class), any(String.class));
        when(paymentOutboxEventRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING))
                .thenReturn(List.of(successEvent));

        paymentOutboxPublisher.publishPendingEvents();

        verify(paymentOutboxEventRepository, times(1)).findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        verify(paymentEventProducer, times(1)).publishPaymentSuccess(any(String.class), any(String.class));
        verify(paymentOutboxEventRepository, never()).save(any(PaymentOutboxEvent.class));
        assertEquals(OutboxStatus.PENDING, successEvent.getStatus());
    }

    @Test
    void publishPendingEvents_multipleEvents() {
        doNothing().when(paymentEventProducer).publishPaymentSuccess(any(String.class), any(String.class));
        doNothing().when(paymentEventProducer).publishPaymentFailed(any(String.class), any(String.class));
        when(paymentOutboxEventRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING))
                .thenReturn(List.of(successEvent, failedEvent));
        when(paymentOutboxEventRepository.save(any(PaymentOutboxEvent.class))).thenReturn(successEvent);

        paymentOutboxPublisher.publishPendingEvents();

        verify(paymentOutboxEventRepository, times(1)).findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        verify(paymentEventProducer, times(1)).publishPaymentSuccess(any(String.class), any(String.class));
        verify(paymentEventProducer, times(1)).publishPaymentFailed(any(String.class), any(String.class));
        verify(paymentOutboxEventRepository, times(2)).save(any(PaymentOutboxEvent.class));
    }
}
