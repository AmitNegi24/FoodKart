package com.foodkart.delivery_service.repository;

import com.foodkart.delivery_service.entity.Delivery;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    @Modifying
    @Query(value = """
        INSERT INTO deliveries
            (order_id, status, created_at, updated_at)
        VALUES
            (:orderId, 'ASSIGNED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        ON CONFLICT (order_id) DO NOTHING
        """, nativeQuery = true)
    void insertIfAbsent(@Param("orderId") Long orderId);

    Optional<Delivery> findByOrderId(Long orderId);

    boolean existsByOrderId(Long orderId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Delivery d WHERE d.orderId = :orderId")
    Optional<Delivery> findByOrderIdForUpdate(
            @Param("orderId") Long orderId
    );
}

