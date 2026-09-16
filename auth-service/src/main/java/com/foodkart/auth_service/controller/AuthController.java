package com.foodkart.auth_service.controller;

import com.foodkart.auth_service.dto.AuthResponseDTO;
import com.foodkart.auth_service.dto.LoginRequestDTO;
import com.foodkart.auth_service.dto.RegisterRequestDTO;
import com.foodkart.auth_service.entity.User;
import com.foodkart.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private User user;

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequestDTO request) {

        String response = authService.register(
                request.getName(),
                request.getEmail(),
                request.getPassword()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO loginRequestDTO){
        log.info("USER TRYING TO LOGIN, VALIDATING CREDENTIALS. USER EMAILID :" + loginRequestDTO.getEmail());
        AuthResponseDTO response = authService.login(loginRequestDTO);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/test")
    public String test() {
        return "Authenticated user";
    }
}