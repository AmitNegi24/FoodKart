package com.foodkart.auth_service.config;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

public class SecretGenerator {

    public static void main(String[] args) throws Exception {

        KeyGenerator keyGenerator =
                KeyGenerator.getInstance("HmacSHA256");

        SecretKey secretKey = keyGenerator.generateKey();

        String base64Secret =
                Base64.getEncoder()
                        .encodeToString(secretKey.getEncoded());

        System.out.println(base64Secret);
    }
}