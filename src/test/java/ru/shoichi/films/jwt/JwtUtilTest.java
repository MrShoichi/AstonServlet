package ru.shoichi.films.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private final SecretKey originalKey = Keys.hmacShaKeyFor("mocksecretkey12345678901234567890".getBytes());



    @Test
    void testGenerateToken() {
        String username = "testUser";
        String role = "Admin";

        String token = JwtUtil.generateToken(username, role);

        assertNotNull(token);
    }

    @Test
    void testValidateTokenValid() {
        String username = "testUser";
        String role = "Admin";
        String token = JwtUtil.generateToken(username, role);
        boolean isValid = JwtUtil.validateToken(token);
        assertTrue(isValid);

    }

    @Test
    void testValidateTokenInvalid() {
        String invalidToken = "invalidToken";

        try (MockedStatic<Jwts> jwtsMockedStatic = mockStatic(Jwts.class)) {
            jwtsMockedStatic.when(() -> Jwts.parser().verifyWith(originalKey).build().parse(invalidToken))
                    .thenThrow(JwtException.class);

            boolean isValid = JwtUtil.validateToken(invalidToken);
            assertFalse(isValid);
        }
    }

    @Test
    void testGetClaimFromToken() {
        String token = "validToken";
        String expectedRole = "Admin";

        try (MockedStatic<JwtUtil> jwtsMockedStatic = mockStatic(JwtUtil.class)) {
            jwtsMockedStatic.when(() -> JwtUtil.getClaimFromToken(token, "role"))
                    .thenReturn(expectedRole);


            String role = JwtUtil.getClaimFromToken(token, "role");
            assertEquals(expectedRole, role);
        }
    }

}
