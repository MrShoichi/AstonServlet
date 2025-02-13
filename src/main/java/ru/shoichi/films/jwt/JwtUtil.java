package ru.shoichi.films.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static ru.shoichi.films.config.AppConfig.secret;

public class JwtUtil {
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 час

    private static final SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

    public static String generateToken(String username, String role) {
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getClaimFromToken(String token, String claimKey) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get(claimKey, String.class);
    }
}
