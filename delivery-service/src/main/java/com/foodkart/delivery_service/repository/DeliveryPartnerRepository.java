package com.foodkart.delivery_service.repository;

import com.foodkart.delivery_service.entity.DeliveryPartner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryPartnerRepository
        extends JpaRepository<DeliveryPartner, Long> {

    Optional<DeliveryPartner> findByPhone(String phone);

    boolean existsByPhone(String phone);

    Optional<DeliveryPartner> findFirstByAvailableTrue();
}
