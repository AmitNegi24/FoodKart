package com.foodKart.restaurant_service.service;

import com.foodKart.restaurant_service.dto.RestaurantRequestDTO;
import com.foodKart.restaurant_service.dto.RestaurantResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RestaurantService {

    RestaurantResponseDTO createRestaurant(RestaurantRequestDTO request);

    Page<RestaurantResponseDTO> getAllRestaurants(Pageable pageable);;

    RestaurantResponseDTO getRestaurantById(Long id);

    RestaurantResponseDTO updateRestaurant(Long id, RestaurantRequestDTO request);

    void deleteRestaurant(Long id);

    Page<RestaurantResponseDTO> getActiveRestaurantsByCity( String city, Pageable pageable);

    Page<RestaurantResponseDTO> getRestaurantsByNameContaining(String name, Pageable pageable);
}