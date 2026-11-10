
package com.foodkart.menu_service.repository;

import com.foodkart.menu_service.entity.FoodItemPopularity;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FoodItemPopularityRepository
        extends JpaRepository<FoodItemPopularity, Long> {

    // Existing popularity ranking
    Page<FoodItemPopularity> findAllByOrderByTotalOrdersDesc(
            Pageable pageable
    );

    // Find popularity record for a particular food item
    Optional<FoodItemPopularity> findByFoodItemId(
            Long foodItemId
    );

    // Return only food items with a positive popularity score
    Page<FoodItemPopularity>
    findByFoodItemIdGreaterThanAndTotalOrdersGreaterThanOrderByTotalOrdersDesc(
            Long foodItemId,
            Long totalOrders,
            Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT p FROM FoodItemPopularity p
    WHERE p.foodItemId = :foodItemId
    """)
    Optional<FoodItemPopularity> findByFoodItemIdForUpdate(
            @Param("foodItemId") Long foodItemId
    );
}
