package com.foodkart.menu_service.repository;

import com.foodkart.menu_service.entity.MenuItem;
import com.foodkart.menu_service.model.FoodItemCategory;
import com.foodkart.menu_service.model.MenuCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    Page<MenuItem> findAll(Pageable pageable);
    MenuItem findByRestaurantIdAndId(Long restaurantId, Long foodItemId);
    Page<MenuItem> findAllByRestaurantId(Long restaurantId, Pageable pageable);
    Page<MenuItem> findAllByRestaurantIdAndAvailableTrue(Long restaurantId, Pageable pageable);
    Page<MenuItem> findAllByRestaurantIdAndMenuCategory(Long restaurantId, MenuCategory menuCategory, Pageable pageable);
    Page<MenuItem> findAllByRestaurantIdAndMenuCategoryAndAvailableTrue(Long restaurantId, MenuCategory menuCategory, Pageable pageable);
    Page<MenuItem> findAllByRestaurantIdAndFoodItemCategory(Long restaurantId, FoodItemCategory foodItemCategory, Pageable pageable);
    Page<MenuItem> findAllByRestaurantIdAndFoodItemCategoryAndAvailableTrue(Long restaurantId, FoodItemCategory foodItemCategory, Pageable pageable);
    Page<MenuItem> findAllByRestaurantIdAndMenuCategoryAndFoodItemCategory(Long restaurantId, MenuCategory menuCategory, FoodItemCategory foodItemCategory, Pageable pageable);
    Page<MenuItem> findAllByRestaurantIdAndMenuCategoryAndFoodItemCategoryAndAvailableTrue(Long restaurantId, MenuCategory menuCategory, FoodItemCategory foodItemCategory, Pageable pageable);
    Page<MenuItem> findAllByRestaurantIdAndPriceBetween(Long restaurantId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    void deleteByRestaurantIdAndId(Long restaurantId, Long menuId);
}
