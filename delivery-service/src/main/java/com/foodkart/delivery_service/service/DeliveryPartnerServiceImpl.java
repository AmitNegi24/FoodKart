package com.foodkart.delivery_service.service;

import com.foodkart.delivery_service.dto.CreateDeliveryPartnerRequestDTO;
import com.foodkart.delivery_service.dto.DeliveryPartnerResponseDTO;
import com.foodkart.delivery_service.entity.DeliveryPartner;
import com.foodkart.delivery_service.repository.DeliveryPartnerRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryPartnerServiceImpl implements DeliveryPartnerService {

    private final DeliveryPartnerRepository deliveryPartnerRepository;

    @Override
    @Transactional
    public DeliveryPartnerResponseDTO registerPartner(
            CreateDeliveryPartnerRequestDTO request) {

        if (deliveryPartnerRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException(
                    "A delivery partner with this phone number already exists"
            );
        }

        DeliveryPartner partner = DeliveryPartner.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .available(true)
                .build();

        DeliveryPartner savedPartner =
                deliveryPartnerRepository.save(partner);

        return DeliveryPartnerResponseDTO.builder()
                .id(savedPartner.getId())
                .name(savedPartner.getName())
                .phone(savedPartner.getPhone())
                .available(savedPartner.isAvailable())
                .build();
    }
}
