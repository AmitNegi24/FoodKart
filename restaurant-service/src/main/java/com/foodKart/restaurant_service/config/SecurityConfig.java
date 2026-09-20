package com.foodKart.restaurant_service.config;

import com.foodKart.restaurant_service.security.JWTFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/restaurants/create",
                                "/restaurants/update/{id}",
                                "/restaurants/delete/{id}")
                        .authenticated()
                        .requestMatchers("/actuator/health",
                                "/restaurants/getAllRestaurants",
                                "/restaurants/getActiveRestaurantsByCity",
                                "/restaurants/getRestaurantsByNameContaining",
                                "/restaurants/getRestaurantsByCity",
                                "/restaurants/{id}")
                        .permitAll()

                        .anyRequest()
                        .authenticated()
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }
}