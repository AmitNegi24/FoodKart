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
    Page<MenuItem> findAllByRestaurantIdAndFoodItemAvailableTrue(Long restaurantId, Pageable pageable);
    Page<MenuItem> findAllByRestaurantIdAndMenuCategory(Long restaurantId, MenuCategory menuCategory, Pageable pageable);
    Page<MenuItem> findAllByRestaurantIdAndMenuCategoryAndFoodItemAvailableTrue(Long restaurantId, MenuCategory menuCategory, Pageable pageable);
    Page<MenuItem> findAllByRestaurantIdAndFoodItemCategory(Long restaurantId, FoodItemCategory foodItemCategory, Pageable pageable);
    Page<MenuItem> findAllByRestaurantIdAndFoodItemCategoryAndFoodItemAvailableTrue(Long restaurantId, FoodItemCategory foodItemCategory, Pageable pageable);
    Page<MenuItem> findAllByRestaurantIdAndMenuCategoryAndFoodItemCategory(Long restaurantId, MenuCategory menuCategory, FoodItemCategory foodItemCategory, Pageable pageable);
    Page<MenuItem> findAllByRestaurantIdAndMenuCategoryAndFoodItemCategoryAndFoodItemAvailableTrue(Long restaurantId, MenuCategory menuCategory, FoodItemCategory foodItemCategory, Pageable pageable);
    Page<MenuItem> findAllByRestaurantIdAndFoodItemPriceBetween(Long restaurantId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    void deleteByRestaurantIdAndId(Long restaurantId, Long menuId);
    boolean existsByRestaurantIdAndId(Long restaurantId, Long id);
}
