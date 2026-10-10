package com.foodkart.delivery_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignDeliveryPartnerRequestDTO {

    @NotNull
    private Long deliveryPartnerId;
}
