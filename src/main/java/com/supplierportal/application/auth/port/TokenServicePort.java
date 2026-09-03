package com.supplierportal.application.auth.port;

public interface TokenServicePort {
    String generateAccessToken(String username, String role);
    String extractUsername(String token);
    String extractRole(String token);
}
