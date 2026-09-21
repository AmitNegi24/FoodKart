package com.foodkart.payment_service.service;

import com.foodkart.payment_service.dto.PaymentRequestDTO;
import com.foodkart.payment_service.dto.PaymentResponseDTO;

public interface PaymentService {

    PaymentResponseDTO processPayment(PaymentRequestDTO request);

    PaymentResponseDTO getPaymentByOrderId(Long orderId);
}