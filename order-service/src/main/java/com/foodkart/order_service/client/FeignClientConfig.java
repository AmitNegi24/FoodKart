package com.foodkart.order_service.client;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor jwtRequestInterceptor() {

        return requestTemplate -> {

            ServletRequestAttributes attributes =
                    (ServletRequestAttributes)
                            RequestContextHolder.getRequestAttributes();

            if (attributes == null) {
                return;
            }

            HttpServletRequest request =
                    attributes.getRequest();

            String authorizationHeader =
                    request.getHeader("Authorization");

            System.out.println(
                    "Feign Authorization header present: "
                            + (authorizationHeader != null)
            );

            if (authorizationHeader != null &&
                    authorizationHeader.startsWith("Bearer ")) {

                requestTemplate.header(
                        "Authorization",
                        authorizationHeader
                );
            }
        };
    }
}



