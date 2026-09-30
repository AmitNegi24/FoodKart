package com.foodkart.payment_service.service;

import com.foodkart.payment_service.dto.PaymentFailedEvent;
import com.foodkart.payment_service.dto.PaymentRequestDTO;
import com.foodkart.payment_service.dto.PaymentResponseDTO;
import com.foodkart.payment_service.dto.PaymentSuccessEvent;
import com.foodkart.payment_service.entity.OutboxStatus;
import com.foodkart.payment_service.entity.Payment;
import com.foodkart.payment_service.entity.PaymentOutboxEvent;
import com.foodkart.payment_service.entity.PaymentStatus;
import com.foodkart.payment_service.repository.PaymentOutboxEventRepository;
import com.foodkart.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentOutboxEventRepository paymentOutboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {

        // Idempotency check
        Payment existingPayment =
                paymentRepository.findByOrderId(request.getOrderId())
                        .orElse(null);

        if (existingPayment != null) {

            System.out.println(
                    "Payment already exists for order: "
                            + request.getOrderId()
            );

            return mapToResponse(existingPayment);
        }

        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .userEmailId(request.getUserEmailId())
                .amount(request.getAmount())
                .status(PaymentStatus.PENDING)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // Payment failure simulation
        if (request.getAmount().compareTo(new BigDecimal("5000")) > 0) {

            savedPayment.setStatus(PaymentStatus.FAILED);

            savedPayment = paymentRepository.save(savedPayment);

            PaymentFailedEvent event =
                    PaymentFailedEvent.builder()
                            .orderId(savedPayment.getOrderId())
                            .userEmailId(savedPayment.getUserEmailId())
                            .reason("Payment amount exceeds limit")
                            .build();

            saveOutboxEvent(
                    savedPayment.getOrderId(),
                    "PAYMENT_FAILED",
                    event
            );

            return mapToResponse(savedPayment);
        }

        // Payment success
        savedPayment.setStatus(PaymentStatus.SUCCESS);

        savedPayment = paymentRepository.save(savedPayment);

        PaymentSuccessEvent event =
                PaymentSuccessEvent.builder()
                        .paymentId(savedPayment.getId())
                        .orderId(savedPayment.getOrderId())
                        .userEmailId(savedPayment.getUserEmailId())
                        .amount(savedPayment.getAmount())
                        .build();

        saveOutboxEvent(
                savedPayment.getOrderId(),
                "PAYMENT_SUCCESS",
                event
        );

        return mapToResponse(savedPayment);
    }

    private void saveOutboxEvent(
            Long orderId,
            String eventType,
            Object event
    ) {
        try {

            String payload =
                    objectMapper.writeValueAsString(event);

            PaymentOutboxEvent outboxEvent =
                    PaymentOutboxEvent.builder()
                            .aggregateId(orderId)
                            .eventType(eventType)
                            .payload(payload)
                            .status(OutboxStatus.PENDING)
                            .build();

            paymentOutboxEventRepository.save(outboxEvent);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to create payment outbox event",
                    e
            );
        }
    }

    @Override
    public PaymentResponseDTO getPaymentByOrderId(Long orderId) {

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Payment not found for order: " + orderId
                        )
                );

        return mapToResponse(payment);
    }

    private PaymentResponseDTO mapToResponse(Payment payment) {

        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .userEmailId(payment.getUserEmailId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}