package com.foodkart.order_service.repository;

import com.foodkart.order_service.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(value = """
    SELECT
        oi.menu_item_id,
        COUNT(
            DISTINCT (
                o.user_id,
                DATE(o.delivered_at)
            )
        ) AS popularity_count
    FROM order_items oi
    JOIN orders o ON oi.order_id = o.id
    WHERE o.status = 'DELIVERED'
      AND o.delivered_at >= :startTime
      AND o.delivered_at < :endTime
    GROUP BY oi.menu_item_id
    ORDER BY popularity_count DESC
    """, nativeQuery = true)
    List<Object[]> findWeeklyFoodItemPopularity(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

}