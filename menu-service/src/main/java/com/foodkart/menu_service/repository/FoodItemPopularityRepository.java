package com.foodkart.menu_service.repository;


import com.foodkart.menu_service.entity.FoodItemPopularity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodItemPopularityRepository
        extends JpaRepository<FoodItemPopularity, Long> {

    Page<FoodItemPopularity> findAllByOrderByTotalOrdersDesc(
            Pageable pageable
    );
}

