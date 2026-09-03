package com.supplierportal.infrastructure.security.jwt;

import com.supplierportal.application.auth.port.TokenServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenServiceAdapter implements TokenServicePort {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public String generateAccessToken(String username, String role) {
        return jwtTokenProvider.generateAccessToken(username, role);
    }

    @Override
    public String extractUsername(String token) {
        return jwtTokenProvider.getUsernameFromToken(token);
    }

    @Override
    public String extractRole(String token) {
        return jwtTokenProvider.getRoleFromToken(token);
    }
}
