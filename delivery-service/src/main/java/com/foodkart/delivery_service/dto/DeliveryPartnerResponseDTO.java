package com.foodkart.delivery_service.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeliveryPartnerResponseDTO {

    private Long id;
    private String name;
    private String phone;
    private boolean available;
}
