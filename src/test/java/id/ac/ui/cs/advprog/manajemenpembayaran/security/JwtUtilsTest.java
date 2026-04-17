package id.ac.ui.cs.advprog.manajemenpembayaran.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "this-is-a-test-secret-key-with-32-chars-min");
    }

    @Test
    void validateTokenShouldReturnTrueForValidToken() {
        String token = Jwts.builder()
                .subject("tester@example.com")
                .claim("role", "ADMIN")
                .signWith(Keys.hmacShaKeyFor("this-is-a-test-secret-key-with-32-chars-min".getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void validateTokenShouldReturnFalseForInvalidToken() {
        assertFalse(jwtUtils.validateToken("invalid.token.value"));
    }

    @Test
    void getClaimsShouldReturnEmailAndRole() {
        String token = Jwts.builder()
                .subject("user@mysawit.id")
                .claim("role", "MANDOR")
                .signWith(Keys.hmacShaKeyFor("this-is-a-test-secret-key-with-32-chars-min".getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertEquals("user@mysawit.id", jwtUtils.getEmailFromToken(token));
        assertEquals("MANDOR", jwtUtils.getRoleFromToken(token));
    }
}
