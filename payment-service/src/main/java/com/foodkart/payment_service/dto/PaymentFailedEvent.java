package com.foodkart.payment_service.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentFailedEvent {

    private Long orderId;

    private String userEmailId;

    private String reason;

    private String correlationId;
}