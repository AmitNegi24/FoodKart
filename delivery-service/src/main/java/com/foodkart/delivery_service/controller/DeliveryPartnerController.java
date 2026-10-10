package com.foodkart.delivery_service.controller;

import com.foodkart.delivery_service.dto.CreateDeliveryPartnerRequestDTO;
import com.foodkart.delivery_service.dto.DeliveryPartnerResponseDTO;
import com.foodkart.delivery_service.service.DeliveryPartnerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/delivery-partners")
@RequiredArgsConstructor
public class DeliveryPartnerController {

    private final DeliveryPartnerService deliveryPartnerService;

    @PostMapping
    public ResponseEntity<DeliveryPartnerResponseDTO> registerPartner(
            @Valid @RequestBody CreateDeliveryPartnerRequestDTO request) {

        DeliveryPartnerResponseDTO response =
                deliveryPartnerService.registerPartner(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
