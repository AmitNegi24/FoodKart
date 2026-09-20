package com.foodkart.menu_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "foodkart_menu_items",
        indexes = {
                @Index(name = "idx_menu_restaurant_id", columnList = "restaurant_id"),
                @Index(name = "idx_menu_category", columnList = "category")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private String menuCategory;

    @Column(nullable = false, length = 100)
    private String foodItemName;

    @Column(length = 500)
    private String foodItemDescription;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal foodItemPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private String foodItemCategory;

    @Column(nullable = false)
    private Boolean foodItemAvailable = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}