package com.foodKart.restaurant_service.repository;

import com.foodKart.restaurant_service.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}
