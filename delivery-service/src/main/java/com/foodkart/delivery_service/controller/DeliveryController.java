package com.foodkart.delivery_service.controller;

import com.foodkart.delivery_service.dto.AssignDeliveryPartnerRequestDTO;
import com.foodkart.delivery_service.dto.DeliveryResponseDTO;
import com.foodkart.delivery_service.dto.UpdateDeliveryStatusRequestDTO;
import com.foodkart.delivery_service.service.DeliveryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    // Get delivery details using order ID
    @GetMapping("/order/{orderId}")
    public ResponseEntity<DeliveryResponseDTO> getDeliveryByOrderId(
            @PathVariable Long orderId) {

        DeliveryResponseDTO response =
                deliveryService.getDeliveryByOrderId(orderId);

        return ResponseEntity.ok(response);
    }

    // Update delivery status
    @PatchMapping("/order/{orderId}/status")
    public ResponseEntity<DeliveryResponseDTO> updateDeliveryStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateDeliveryStatusRequestDTO request) {

        DeliveryResponseDTO response =
                deliveryService.updateDeliveryStatus(
                        orderId,
                        request.getStatus()
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/order/{orderId}/assign")
    public ResponseEntity<DeliveryResponseDTO> assignPartner(
            @PathVariable Long orderId,
            @RequestBody AssignDeliveryPartnerRequestDTO request) {

        DeliveryResponseDTO response =
                deliveryService.assignPartner(
                        orderId,
                        request.getDeliveryPartnerId()
                );

        return ResponseEntity.ok(response);
    }

}

