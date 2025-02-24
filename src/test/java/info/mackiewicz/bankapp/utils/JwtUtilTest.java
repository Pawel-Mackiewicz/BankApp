package info.mackiewicz.bankapp.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private static final String TEST_EMAIL = "test@example.com";

    @Test
    void generateToken_ShouldCreateValidToken() {
        // Act
        String token = JwtUtil.generateToken(TEST_EMAIL);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void parseToken_ShouldReturnValidClaims_WhenTokenIsValid() {
        // Arrange
        String token = JwtUtil.generateToken(TEST_EMAIL);

        // Act
        Claims claims = JwtUtil.parseToken(token);

        // Assert
        assertNotNull(claims);
        assertEquals(TEST_EMAIL, claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void parseToken_ShouldThrowException_WhenTokenIsMalformed() {
        // Arrange
        String malformedToken = "invalid.token.string";

        // Act & Assert
        assertThrows(MalformedJwtException.class, () -> JwtUtil.parseToken(malformedToken));
    }

    @Test
    void parseToken_ShouldThrowException_WhenTokenHasInvalidSignature() {
        // Arrange
        String tokenWithInvalidSignature = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                "eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0IjoxNTE2MjM5MDIyfQ." +
                "invalidSignature";

        // Act & Assert
        assertThrows(SignatureException.class, () -> JwtUtil.parseToken(tokenWithInvalidSignature));
    }

    @Test
    void tokenGeneration_ShouldSetCorrectExpiration() {
        // Arrange
        Date now = new Date();

        // Act
        String token = JwtUtil.generateToken(TEST_EMAIL);
        Claims claims = JwtUtil.parseToken(token);

        // Assert
        assertTrue(claims.getExpiration().after(now));
        long expectedDuration = 86400000; // 1 dzień w milisekundach
        long actualDuration = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();
        assertEquals(expectedDuration, actualDuration, 1000); // dopuszczamy 1 sekundę różnicy
    }

    @Test
    void tokenGeneration_ShouldSetCorrectIssuedAt() {
        // Arrange
        long beforeGenerationSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        
        // Act
        String token = JwtUtil.generateToken(TEST_EMAIL);
        Claims claims = JwtUtil.parseToken(token);
        long afterGenerationSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        // Assert
        long issuedAtSeconds = TimeUnit.MILLISECONDS.toSeconds(claims.getIssuedAt().getTime());
        assertTrue(issuedAtSeconds >= beforeGenerationSeconds && issuedAtSeconds <= afterGenerationSeconds,
                String.format("IssuedAt (%d seconds) should be between %d and %d seconds", 
                        issuedAtSeconds, beforeGenerationSeconds, afterGenerationSeconds));
    }

    @Test
    void differentEmails_ShouldGenerateDifferentTokens() {
        // Arrange
        String email1 = "user1@example.com";
        String email2 = "user2@example.com";

        // Act
        String token1 = JwtUtil.generateToken(email1);
        String token2 = JwtUtil.generateToken(email2);

        // Assert
        assertNotEquals(token1, token2, "Tokens for different emails should be different");
    }

    @Test
    void sameEmail_ShouldGenerateDifferentTokens() throws InterruptedException {
        // Arrange
        String email = "test@example.com";

        // Act
        String token1 = JwtUtil.generateToken(email);
        Thread.sleep(1000); // Czekamy 1 sekundę, aby mieć różne znaczniki czasowe
        String token2 = JwtUtil.generateToken(email);

        // Assert
        assertNotEquals(token1, token2, "Tokens generated at different times should be different");
    }

    @Test
    void generateToken_ShouldCreateTokenWithCorrectUserEmail() {
        // Arrange
        String expectedEmail = "specific.user@example.com";

        // Act
        String token = JwtUtil.generateToken(expectedEmail);
        Claims claims = JwtUtil.parseToken(token);

        // Assert
        assertEquals(expectedEmail, claims.getSubject());
    }
}