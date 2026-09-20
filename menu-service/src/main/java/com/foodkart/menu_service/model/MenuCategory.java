package com.foodkart.menu_service.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum MenuCategory {
    STARTER,
    MAIN_COURSE,
    DESSERT,
    BEVERAGE;
// refer to FoodItemCategory enum class to understand below method
    @JsonCreator
    public static MenuCategory fromString(String value) {
        return MenuCategory.valueOf(value.toUpperCase());
    }
}
