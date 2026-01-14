package com.hms.booking.util;

import com.hms.booking.config.security.JwtProperties;
import com.hms.booking.dao.UserRepository;
import com.hms.booking.domain.User;
import com.nimbusds.jose.JWSAlgorithm;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

@Service
public class TokenUtil {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;

    public TokenUtil(JwtEncoder jwtEncoder, JwtProperties jwtProperties) {
        this.jwtEncoder = jwtEncoder;
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        Instant expiry = now.plus(jwtProperties.getExpiration(), ChronoUnit.HOURS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiresAt(expiry)
                .subject(authentication.getName())
                .build();

        JwsHeader headers = JwsHeader.with(JWSAlgorithm.HS256::getName)
                .keyId(jwtProperties.getKeyId())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(headers, claims)).getTokenValue();
    }

    public String generateToken(String username) {
        Instant now = Instant.now();
        Instant expiry = now.plus(jwtProperties.getExpiration(), ChronoUnit.HOURS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiresAt(expiry)
                .subject(username)
                .build();

        JwsHeader headers = JwsHeader.with(JWSAlgorithm.HS256::getName)
                .keyId(jwtProperties.getKeyId())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(headers, claims)).getTokenValue();
    }

    public Jwt getToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken) {
            return (Jwt) authentication.getPrincipal();
        }
        return null;
    }
}