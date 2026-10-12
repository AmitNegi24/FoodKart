
package com.foodkart.delivery_service.repository;

import com.foodkart.delivery_service.entity.DeliveryPartner;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DeliveryPartnerRepository
        extends JpaRepository<DeliveryPartner, Long> {

    Optional<DeliveryPartner> findByPhone(String phone);

    boolean existsByPhone(String phone);

    Optional<DeliveryPartner> findFirstByAvailableTrue();

    // Lock the selected partner row until the transaction finishes
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM DeliveryPartner p WHERE p.id = :partnerId")
    Optional<DeliveryPartner> findByIdForUpdate(
            @Param("partnerId") Long partnerId
    );
}
