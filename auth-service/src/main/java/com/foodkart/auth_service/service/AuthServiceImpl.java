package com.foodkart.auth_service.service;

import com.foodkart.auth_service.dto.AuthResponseDTO;
import com.foodkart.auth_service.dto.LoginRequestDTO;
import com.foodkart.auth_service.entity.User;
import com.foodkart.auth_service.exception.AuthFailedException;
import com.foodkart.auth_service.repository.UserRepository;
import com.foodkart.auth_service.security.JWTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String register(String name, String email, String password) {

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role("CUSTOMER")
                .active(true)
                .build();

        userRepository.save(user);

        return "User registered successfully";
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {

        log.info("1. Authentication attempt");

        try {
            // If this succeeds, the credentials are valid. No redundant .isAuthenticated() check needed.
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            log.info("Authentication successful for email: {}", request.getEmail());

            // Extract username/email from the principal if necessary, or use the request email
            String token = jwtService.generateToken(authentication.getName());
            return new AuthResponseDTO(token);

        }
        catch (Exception e) {
            log.error("Authentication failed completely. Root cause: ", e); // This will print the exact error in your console
            throw new AuthFailedException("AuthService.INVALID_CREDENTIALS");
        }
    }
}
