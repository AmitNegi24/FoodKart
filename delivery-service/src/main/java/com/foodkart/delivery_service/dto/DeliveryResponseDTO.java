package com.foodkart.delivery_service.dto;

import com.foodkart.delivery_service.entity.DeliveryStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryResponseDTO {

    private Long deliveryId;

    private Long orderId;

    private DeliveryStatus status;

    private Long deliveryPartnerId;

    private String deliveryPartnerName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
