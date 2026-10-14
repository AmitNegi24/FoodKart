
package com.foodkart.delivery_service.service;

import com.foodkart.delivery_service.dto.DeliveryResponseDTO;
import com.foodkart.delivery_service.entity.Delivery;
import com.foodkart.delivery_service.entity.DeliveryPartner;
import com.foodkart.delivery_service.entity.DeliveryStatus;
import com.foodkart.delivery_service.kafka.DeliveryStatusEventProducer;
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
    private final DeliveryStatusEventProducer deliveryStatusEventProducer;

    // 1. Create a delivery if one does not already exist
    @Override
    @Transactional
    public DeliveryResponseDTO createDelivery(Long orderId) {

        // Insert a delivery only if this order does not already have one.
        deliveryRepository.insertIfAbsent(orderId);

        // Fetch the delivery, whether it was newly inserted or already existed.
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Delivery could not be created for orderId: " + orderId
                        )
                );

        return mapToResponse(delivery);
    }

    // 2. Get delivery details using the order ID
    @Override
    @Transactional(readOnly = true)
    public DeliveryResponseDTO getDeliveryByOrderId(Long orderId) {

        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Delivery not found for orderId: " + orderId
                        )
                );

        return mapToResponse(delivery);
    }

    // 3. Update delivery status and release partner when finished
    @Override
    @Transactional
    public DeliveryResponseDTO updateDeliveryStatus(
            Long orderId,
            DeliveryStatus newStatus) {

        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Delivery not found for orderId: " + orderId
                        )
                );

        // Require an assigned partner for progress, but allow cancellation
        if (newStatus != DeliveryStatus.CANCELLED
                && delivery.getDeliveryPartner() == null) {
            throw new IllegalStateException(
                    "Cannot update delivery status without an assigned delivery partner"
            );
        }

        DeliveryStatus currentStatus = delivery.getStatus();

        // Validate allowed status transitions
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

        // Release the partner when delivery finishes or is cancelled
        if (newStatus == DeliveryStatus.DELIVERED
                || newStatus == DeliveryStatus.CANCELLED) {

            DeliveryPartner assignedPartner = delivery.getDeliveryPartner();

            if (assignedPartner != null) {
                assignedPartner.setAvailable(true);
                deliveryPartnerRepository.save(assignedPartner);
            }
        }

        delivery.setStatus(newStatus);

        Delivery updatedDelivery = deliveryRepository.save(delivery);

        deliveryStatusEventProducer.publishStatusUpdate(
                orderId,
                newStatus.name()
        );

        return mapToResponse(updatedDelivery);
    }

    // 4. Assign an available delivery partner
    @Override
    @Transactional
    public DeliveryResponseDTO assignPartner(
            Long orderId,
            Long deliveryPartnerId) {

        // 1. Lock the delivery row first
        Delivery delivery = deliveryRepository
                .findByOrderIdForUpdate(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Delivery not found for orderId: " + orderId
                        )
                );

        // 2. Check whether this delivery already has a partner
        if (delivery.getDeliveryPartner() != null) {
            throw new IllegalStateException(
                    "A partner is already assigned to this delivery"
            );
        }

        // 3. Only ASSIGNED deliveries can receive a partner
        if (delivery.getStatus() != DeliveryStatus.ASSIGNED) {
            throw new IllegalStateException(
                    "Partner can only be assigned to a delivery in ASSIGNED status"
            );
        }

        // 4. Lock the selected partner row
        DeliveryPartner partner = deliveryPartnerRepository
                .findByIdForUpdate(deliveryPartnerId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Delivery partner not found: " + deliveryPartnerId
                        )
                );

        // 5. Check availability after obtaining the lock
        if (!partner.isAvailable()) {
            throw new IllegalStateException(
                    "Delivery partner is not available"
            );
        }

        // 6. Mark partner unavailable
        partner.setAvailable(false);

        // 7. Assign partner to delivery
        delivery.setDeliveryPartner(partner);

        // 8. Save both changes in the same transaction
        deliveryPartnerRepository.save(partner);
        Delivery savedDelivery = deliveryRepository.save(delivery);

        return mapToResponse(savedDelivery);
    }
    // 5. Convert Delivery entity to response DTO
    private DeliveryResponseDTO mapToResponse(Delivery delivery) {

        DeliveryPartner partner = delivery.getDeliveryPartner();

        return DeliveryResponseDTO.builder()
                .deliveryId(delivery.getId())
                .orderId(delivery.getOrderId())
                .status(delivery.getStatus())
                .deliveryPartnerId(
                        partner != null ? partner.getId() : null
                )
                .deliveryPartnerName(
                        partner != null ? partner.getName() : null
                )
                .createdAt(delivery.getCreatedAt())
                .updatedAt(delivery.getUpdatedAt())
                .build();
    }
}
