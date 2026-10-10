
package com.foodkart.delivery_service.service;

import com.foodkart.delivery_service.dto.DeliveryResponseDTO;
import com.foodkart.delivery_service.entity.Delivery;
import com.foodkart.delivery_service.entity.DeliveryPartner;
import com.foodkart.delivery_service.entity.DeliveryStatus;
import com.foodkart.delivery_service.repository.DeliveryPartnerRepository;
import com.foodkart.delivery_service.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryPartnerRepository deliveryPartnerRepository;
    @Override
    @Transactional
    public DeliveryResponseDTO createDelivery(Long orderId) {

        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseGet(() -> deliveryRepository.save(
                        Delivery.builder()
                                .orderId(orderId)
                                .status(DeliveryStatus.ASSIGNED)
                                .build()
                ));

        return mapToResponse(delivery);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryResponseDTO getDeliveryByOrderId(Long orderId) {

        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Delivery not found for order: " + orderId
                        )
                );

        return mapToResponse(delivery);
    }

    @Override
    @Transactional
    public DeliveryResponseDTO updateDeliveryStatus(
            Long orderId,
            DeliveryStatus newStatus) {

        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Delivery not found for orderId: " + orderId
                        )
                );

        DeliveryStatus currentStatus = delivery.getStatus();

        // Validate allowed transitions
        boolean validTransition = switch (currentStatus) {
            case ASSIGNED ->
                    newStatus == DeliveryStatus.PICKED_UP
                            || newStatus == DeliveryStatus.CANCELLED;

            case PICKED_UP ->
                    newStatus == DeliveryStatus.OUT_FOR_DELIVERY
                            || newStatus == DeliveryStatus.CANCELLED;

            case OUT_FOR_DELIVERY ->
                    newStatus == DeliveryStatus.DELIVERED;

            case DELIVERED, CANCELLED -> false;
        };

        if (!validTransition) {
            throw new IllegalArgumentException(
                    "Invalid delivery status transition: "
                            + currentStatus + " -> " + newStatus
            );
        }

        delivery.setStatus(newStatus);

        Delivery updatedDelivery = deliveryRepository.save(delivery);

        return mapToResponse(updatedDelivery);
    }


    private DeliveryResponseDTO mapToResponse(Delivery delivery) {

        return DeliveryResponseDTO.builder()
                .deliveryId(delivery.getId())
                .orderId(delivery.getOrderId())
                .status(delivery.getStatus())
                .deliveryPartnerId(
                        delivery.getDeliveryPartner() != null
                                ? delivery.getDeliveryPartner().getId()
                                : null
                )
                .deliveryPartnerName(
                        delivery.getDeliveryPartner() != null
                                ? delivery.getDeliveryPartner().getName()
                                : null
                )
                .createdAt(delivery.getCreatedAt())
                .updatedAt(delivery.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public DeliveryResponseDTO assignPartner(
            Long orderId,
            Long deliveryPartnerId) {

        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Delivery not found for orderId: " + orderId
                        )
                );

        if (delivery.getDeliveryPartner() != null) {
            throw new IllegalStateException(
                    "A partner is already assigned to this delivery"
            );
        }

        if (delivery.getStatus() != DeliveryStatus.ASSIGNED) {
            throw new IllegalStateException(
                    "Partner can only be assigned to a delivery in ASSIGNED status"
            );
        }

        DeliveryPartner partner = deliveryPartnerRepository
                .findById(deliveryPartnerId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Delivery partner not found: " + deliveryPartnerId
                        )
                );

        if (!partner.isAvailable()) {
            throw new IllegalStateException(
                    "Delivery partner is not available"
            );
        }

        partner.setAvailable(false);
        delivery.setDeliveryPartner(partner);

        deliveryPartnerRepository.save(partner);
        Delivery savedDelivery = deliveryRepository.save(delivery);

        return mapToResponse(savedDelivery);
    }

}