package com.foodkart.menu_service.entity;

import com.foodkart.menu_service.model.FoodItemCategory;
import com.foodkart.menu_service.model.MenuCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "menu_items",
        indexes = {
                @Index(name = "idx_menu_restaurant_id", columnList = "restaurant_id")
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
    @NotNull
    @Column(nullable = false)
    private MenuCategory menuCategory;

    @Column(nullable = false, length = 100)
    private String foodItemName;

    @Column(length = 500)
    private String foodItemDescription;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal foodItemPrice;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private FoodItemCategory foodItemCategory;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean foodItemAvailable;

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