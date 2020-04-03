package com.example.demo.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtTokenUtils {

    static final String CLAIM_KEY_USERNAME = "sub";
    static final String CLAIM_KEY_ROLE = "role";
    static final String CLAIM_KEY_CREATED = "created";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String getUsername(String token) {
        String username = null;
        Optional<Claims> claims = getClaimsFromToken(token);
        if (claims.isPresent()) {
            username = claims.get().getSubject();
        }

        return username;
    }

    public LocalDateTime getExpiration(String token) {
        LocalDateTime expiration = null;
        Optional<Claims> claims = getClaimsFromToken(token);
        if (claims.isPresent()) {
            expiration = claims.get().getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }

        return expiration;
    }

    public Boolean isValidToken(String token) {
        return !isExpiredToken(token);
    }

    public String getToken(UserDetails details) {

        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, details.getUsername());
        details.getAuthorities().forEach(auth -> claims.put(CLAIM_KEY_ROLE, auth.getAuthority()));
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    private boolean isExpiredToken(String token) {
        LocalDateTime expiration = getExpiration(token);
        if (expiration == null) {
            return false;
        }

        return expiration.isBefore(LocalDateTime.now());
    }

    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder().setClaims(claims).setExpiration(fromLocalDateTime(generateExpiration()))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    private LocalDateTime generateExpiration() {
        return LocalDateTime.now().plus(expiration, ChronoUnit.SECONDS);
    }

    private Optional<Claims> getClaimsFromToken(String token) {

        Optional<Claims> claims = Optional.empty();
        try {
            claims = Optional.ofNullable(Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody());
        } catch (Exception e) {
            claims = Optional.empty();
        }
        return claims;
    }

    private Date fromLocalDateTime(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
