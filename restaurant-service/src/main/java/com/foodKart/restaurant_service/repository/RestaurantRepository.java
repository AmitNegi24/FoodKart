package com.foodKart.restaurant_service.repository;

import com.foodKart.restaurant_service.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Page<Restaurant> findAll(Pageable pageable);
    Page<Restaurant> findByCityAndActiveTrue(String city, Pageable pageable);
    Page<Restaurant> findByNameContainingAndActiveTrue(
            String name,
            Pageable pageable
    );
}
