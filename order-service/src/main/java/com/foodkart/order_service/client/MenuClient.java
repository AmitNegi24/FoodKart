package com.foodkart.order_service.client;

import java.math.BigDecimal;

public interface MenuClient {

    BigDecimal getFoodPrice(Long foodId);
}