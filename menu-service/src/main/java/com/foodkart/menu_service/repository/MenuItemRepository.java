package com.foodkart.menu_service.repository;

import com.foodkart.menu_service.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    Page<MenuItem> findAll(Pageable pageable);
    Page<MenuItem> findAllByRestaurantId(Long restaurantId, Pageable pageable);
    MenuItem findByRestaurantIdAndId(Long restaurantId, Long menuId);
    Page<MenuItem> findByRestaurantIdAndCategory(Long restaurantId, String category, Pageable pageable);
    Page<MenuItem> findByRestaurantIdAndCategoryAndAvailableTrue(Long restaurantId, String category, Pageable pageable);
    Page<MenuItem> findAllMenusByRestaurantIdAndAvailableTrue(Long restaurantId, Pageable pageable);
    Page<MenuItem> findByRestaurantIdAndPriceBetween(Long restaurantId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    void deleteByRestaurantIdAndId(Long restaurantId, Long menuId);
}
