package com.foodkart.auth_service.service;

import com.foodkart.auth_service.dto.AuthResponseDTO;
import com.foodkart.auth_service.dto.LoginRequestDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public interface AuthService {

    String register(String name, String email, String password);

    AuthResponseDTO login(LoginRequestDTO request);
}