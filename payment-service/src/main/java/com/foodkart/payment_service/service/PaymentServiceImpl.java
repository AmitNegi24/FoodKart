package com.foodkart.payment_service.service;

import com.foodkart.payment_service.dto.PaymentFailedEvent;
import com.foodkart.payment_service.dto.PaymentRequestDTO;
import com.foodkart.payment_service.dto.PaymentResponseDTO;
import com.foodkart.payment_service.dto.PaymentSuccessEvent;
import com.foodkart.payment_service.entity.Payment;
import com.foodkart.payment_service.entity.PaymentStatus;
import com.foodkart.payment_service.kafka.PaymentEventProducer;
import com.foodkart.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer paymentEventProducer;

    @Override
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {


        // Check if payment already exists for the order and gives idempotency also we added unique constraint on orderId in database level
        //start
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
        //end
        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .userEmailId(request.getUserEmailId())
                .amount(request.getAmount())
                .status(PaymentStatus.PENDING)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // Temporary simulation.
        // Later this will call Razorpay/Stripe/payment gateway.
        if (request.getAmount().compareTo(new BigDecimal("5000")) > 0) {

            savedPayment.setStatus(PaymentStatus.FAILED);

            savedPayment = paymentRepository.save(savedPayment);

            PaymentFailedEvent event =
                    PaymentFailedEvent.builder()
                            .orderId(savedPayment.getOrderId())
                            .userEmailId(savedPayment.getUserEmailId())
                            .reason("Payment amount exceeds limit")
                            .build();

            paymentEventProducer.publishPaymentFailed(event);

            return mapToResponse(savedPayment);
        }
        savedPayment.setStatus(PaymentStatus.SUCCESS);

        savedPayment = paymentRepository.save(savedPayment);

        PaymentSuccessEvent event = PaymentSuccessEvent.builder()
                .paymentId(savedPayment.getId())
                .orderId(savedPayment.getOrderId())
                .userEmailId(savedPayment.getUserEmailId())
                .amount(savedPayment.getAmount())
                .build();

        paymentEventProducer.publishPaymentSuccess(event);

        return mapToResponse(savedPayment);
    }

    @Override
    public PaymentResponseDTO getPaymentByOrderId(Long orderId) {

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Payment not found for order: " + orderId));

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