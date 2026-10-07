package com.foodkart.order_service.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSuccessEvent {

    private Long paymentId;

    private Long orderId;

    private String userEmailId;

    private BigDecimal amount;

    private String correlationId;
}