package com.foodkart.menu_service.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "food_item_popularity")
public class FoodItemPopularity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "food_item_id", nullable = false, unique = true)
    private Long foodItemId;

    @Column(name = "total_orders", nullable = false)
    private Long totalOrders = 0L;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

