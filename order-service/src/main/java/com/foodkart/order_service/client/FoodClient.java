package com.foodkart.order_service.client;

import java.math.BigDecimal;

public interface FoodClient {

    BigDecimal getFoodPrice(Long foodId);
}