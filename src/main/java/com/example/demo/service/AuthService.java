package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final JwtEncoder jwtEncoder;

    @Value("${jwt.expiration.ms}")
    private long jwtExpirationInMs;

    public AuthService(JwtEncoder jwtEncoder, @Value("${jwt.expiration.ms}") long jwtExpirationInMs) {
        this.jwtEncoder = jwtEncoder;;
    }

    public String createToken(Authentication authentication) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self") // should be website address/domain if in prod
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusMillis(jwtExpirationInMs))
                .subject(authentication.getName()) // (username/email)
                // Add roles as a 'scope' claim, space-separated as per OAuth2 conventions
                .claim("scope", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(" ")))
                .build();

        // Encode the JWT
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
