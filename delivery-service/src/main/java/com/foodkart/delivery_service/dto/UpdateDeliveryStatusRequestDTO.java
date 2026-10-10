package com.foodkart.delivery_service.dto;

import com.foodkart.delivery_service.entity.DeliveryStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDeliveryStatusRequestDTO {

    @NotNull(message = "Delivery status is required")
    private DeliveryStatus status;
}