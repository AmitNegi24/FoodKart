package com.foodkart.order_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            System.out.println("========== JWT DEBUG ==========");
            System.out.println("Authorization header missing");
            System.out.println("==============================");

            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        boolean valid = jwtService.isTokenValid(token);

        System.out.println("========== JWT DEBUG ==========");
        System.out.println("Authorization header present: true");
        System.out.println("Token valid: " + valid);

        if (valid) {

            String email =
                    jwtService.extractEmailId(token);

            System.out.println("JWT email: " + email);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            Collections.emptyList()
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            System.out.println(
                    "Authentication set: "
                            + SecurityContextHolder
                            .getContext()
                            .getAuthentication()
            );
        }

        System.out.println("==============================");

        filterChain.doFilter(request, response);
    }
}