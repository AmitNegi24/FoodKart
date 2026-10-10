
package com.foodkart.delivery_service.service;

import com.foodkart.delivery_service.dto.DeliveryResponseDTO;
import com.foodkart.delivery_service.entity.DeliveryStatus;

public interface DeliveryService {

    DeliveryResponseDTO createDelivery(Long orderId);

    DeliveryResponseDTO getDeliveryByOrderId(Long orderId);

    DeliveryResponseDTO updateDeliveryStatus(
            Long orderId,
            DeliveryStatus status
    );
    DeliveryResponseDTO assignPartner(Long orderId, Long deliveryPartnerId);
}