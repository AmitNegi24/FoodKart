package com.foodkart.delivery_service.service;

import com.foodkart.delivery_service.dto.CreateDeliveryPartnerRequestDTO;
import com.foodkart.delivery_service.dto.DeliveryPartnerResponseDTO;

public interface DeliveryPartnerService {

    DeliveryPartnerResponseDTO registerPartner(
            CreateDeliveryPartnerRequestDTO request
    );
}