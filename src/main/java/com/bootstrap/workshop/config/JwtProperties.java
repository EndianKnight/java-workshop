package com.bootstrap.workshop.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT configuration properties.
 * Binds to properties prefixed with "jwt" in application.properties/yaml.
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret,
        long expirationMs) {
    public JwtProperties {
        // Default values
        secret = secret != null ? secret : "your-256-bit-secret-key-for-jwt-signing-must-be-long";
        expirationMs = expirationMs > 0 ? expirationMs : 86400000; // 24 hours
    }
}
