package com.foodkart.menu_service.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum FoodItemCategory {

    VEG,
    NON_VEG;
//this lower method is because what if someone in request send "veg" then it will convert to VEG by using this method
    @JsonCreator
    public static FoodItemCategory fromString(String value) {
        return FoodItemCategory.valueOf(value.toUpperCase());
    }
}
