package info.mackiewicz.bankapp.system.token.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TokenOperationsServiceTest {

    private TokenOperationsService tokenHashingService;

    @BeforeEach
    void setUp() {
        tokenHashingService = new TokenOperationsService();
    }

    @Test
    void generateToken_ShouldGenerateUniqueTokens() {
        // Arrange
        Set<String> tokens = new HashSet<>();
        int numberOfTokens = 1000;

        // Act & Assert
        for (int i = 0; i < numberOfTokens; i++) {
            String token = tokenHashingService.generateToken();
            // Verify token is valid Base64URL and has correct length
            byte[] decoded = Base64.getUrlDecoder().decode(token);
            assertEquals(32, decoded.length, "Token should be 32 bytes when decoded");
            assertTrue(tokens.add(token), "Token should be unique");
        }

        assertEquals(numberOfTokens, tokens.size(), "All generated tokens should be unique");
    }

    @Test
    void hashToken_ShouldGenerateValidHash() {
        // Arrange
        String token = "test-token";

        // Act
        String hash = tokenHashingService.hashToken(token);

        // Assert
        // Verify the hash is a Base64-encoded SHA-256 hash (32 bytes = 44 chars in Base64)
        assertEquals(44, hash.length(), "Hash should be Base64-encoded SHA-256 (32 bytes)");
        assertNotEquals(token, hash, "Hash should be different from input token");
        assertTrue(tokenHashingService.verifyToken(token, hash), "Token should be verifiable with its hash");
    }

    @Test
    void verifyToken_WithInvalidToken_ShouldReturnFalse() {
        // Arrange
        String originalToken = "test-token";
        String hash = tokenHashingService.hashToken(originalToken);
        String wrongToken = "wrong-token";

        // Act
        boolean result = tokenHashingService.verifyToken(wrongToken, hash);

        // Assert
        assertFalse(result, "Invalid token should not be verified");
    }

    @Test
    void hashToken_WithNullInput_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> tokenHashingService.hashToken(null),
                "Null input should throw IllegalArgumentException");
    }

    @Test
    void verifyToken_WithNullInputs_ShouldThrowException() {
        // Arrange
        String validToken = "test-token";
        String validHash = tokenHashingService.hashToken(validToken);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> tokenHashingService.verifyToken(null, validHash),
                "Null token should throw IllegalArgumentException");

        assertThrows(IllegalArgumentException.class,
                () -> tokenHashingService.verifyToken(validToken, null),
                "Null hash should throw IllegalArgumentException");
    }

    @Test
    void generateAndVerifyToken_ShouldWorkTogether() {
        // Arrange
        String token = tokenHashingService.generateToken();
        
        // Act
        String hash = tokenHashingService.hashToken(token);
        boolean isValid = tokenHashingService.verifyToken(token, hash);
        
        // Assert
        assertTrue(isValid, "Generated token should be verifiable with its hash");
    }
}