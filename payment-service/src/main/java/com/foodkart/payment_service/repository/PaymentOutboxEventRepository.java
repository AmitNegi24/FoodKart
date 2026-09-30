package com.foodkart.payment_service.repository;

import com.foodkart.payment_service.entity.OutboxStatus;
import com.foodkart.payment_service.entity.PaymentOutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentOutboxEventRepository
        extends JpaRepository<PaymentOutboxEvent, Long> {

    List<PaymentOutboxEvent> findByStatusOrderByCreatedAtAsc(
            OutboxStatus status
    );
}