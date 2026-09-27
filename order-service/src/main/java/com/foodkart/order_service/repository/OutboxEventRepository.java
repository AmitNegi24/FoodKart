package com.foodkart.order_service.repository;

import com.foodkart.order_service.entity.OutboxEvent;
import com.foodkart.order_service.entity.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    Optional<OutboxEvent> findByEventId(UUID eventId);

    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxStatus status);
}