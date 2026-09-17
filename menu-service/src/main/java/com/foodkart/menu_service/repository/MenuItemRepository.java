package com.foodkart.menu_service.repository;

import com.foodkart.menu_service.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
}
