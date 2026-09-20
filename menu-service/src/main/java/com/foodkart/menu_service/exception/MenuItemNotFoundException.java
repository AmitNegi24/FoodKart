package com.foodkart.menu_service.exception;

public class MenuItemNotFoundException extends RuntimeException {

    // Constructor
    public MenuItemNotFoundException(String message) {
        super(message);
    }
}
