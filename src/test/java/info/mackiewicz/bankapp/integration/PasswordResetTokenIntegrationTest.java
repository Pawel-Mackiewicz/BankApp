package info.mackiewicz.bankapp.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import info.mackiewicz.bankapp.security.exception.InvalidPasswordResetTokenException;
import info.mackiewicz.bankapp.security.exception.TooManyPasswordResetAttemptsException;
import info.mackiewicz.bankapp.security.model.PasswordResetToken;
import info.mackiewicz.bankapp.security.repository.PasswordResetTokenRepository;
import info.mackiewicz.bankapp.security.service.PasswordResetTokenService;
import info.mackiewicz.bankapp.testutils.config.TestConfig;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Import(TestConfig.class)
class PasswordResetTokenIntegrationTest {

    @Autowired
    private PasswordResetTokenService tokenService;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_FULL_NAME = "Test User";

    @Test
    void fullPasswordResetFlow_ShouldWorkCorrectly() {
        // Arrange - Request password reset
        String token = tokenService.createToken(TEST_EMAIL, TEST_FULL_NAME);
        assertNotNull(token);

        // Verify token is stored as hash
        Optional<PasswordResetToken> storedToken = tokenRepository.findAll().stream()
                .filter(t -> t.getUserEmail().equals(TEST_EMAIL))
                .findFirst();
        assertTrue(storedToken.isPresent());
        assertNotEquals(token, storedToken.get().getTokenHash());

        // Act - Validate token
        PasswordResetToken validatedToken = tokenService.validateAndGetToken(token);

        // Assert - Token validation successful
        assertNotNull(validatedToken);
        assertEquals(TEST_EMAIL, validatedToken.getUserEmail());

        // Act - Consume token
        tokenService.consumeToken(token);
        
        // Verify token is marked as used
        Optional<PasswordResetToken> usedToken = tokenRepository.findAll().stream()
                .filter(t -> t.getUserEmail().equals(TEST_EMAIL))
                .findFirst();
        assertTrue(usedToken.isPresent());
        assertTrue(usedToken.get().isUsed());
        assertNotNull(usedToken.get().getUsedAt());
    }

    @Test
    void tokenLimits_ShouldBeEnforced() {
        // Create max number of tokens
        String token1 = tokenService.createToken(TEST_EMAIL, TEST_FULL_NAME);
        String token2 = tokenService.createToken(TEST_EMAIL, TEST_FULL_NAME);
        
        // Assert both tokens are valid
        assertNotNull(token1);
        assertNotNull(token2);
        
        // Try to create one more token
        assertThrows(TooManyPasswordResetAttemptsException.class, () -> {
            tokenService.createToken(TEST_EMAIL, TEST_FULL_NAME);
        });
    }

    @Test
    void expiredTokens_ShouldBeCleanedUp() {
        // Create a token
        String token = tokenService.createToken(TEST_EMAIL, TEST_FULL_NAME);
        
        // Manually expire the token in database
        tokenRepository.findAll().stream()
                .filter(t -> t.getUserEmail().equals(TEST_EMAIL))
                .findFirst()
                .ifPresent(t -> {
                    t.setExpiresAt(LocalDateTime.now().minusHours(1));
                    tokenRepository.save(t);
                });
        
        // Verify token is now invalid
        assertThrows(InvalidPasswordResetTokenException.class, () -> {
            tokenService.validateAndGetToken(token);
        });
        
        // Clean up old tokens
        int deletedCount = tokenService.cleanupOldTokens(0);
        assertTrue(deletedCount > 0);
        
        // Verify token was deleted
        assertEquals(0, tokenRepository.findAll().size());
    }

    @Test
    void multipleValidations_ShouldWork() {
        // Create token
        String token = tokenService.createToken(TEST_EMAIL, TEST_FULL_NAME);
        
        // Multiple validations should work until consumed
        PasswordResetToken validToken1 = tokenService.validateAndGetToken(token);
        PasswordResetToken validToken2 = tokenService.validateAndGetToken(token);
        PasswordResetToken validToken3 = tokenService.validateAndGetToken(token);
        
        assertNotNull(validToken1);
        assertNotNull(validToken2);
        assertNotNull(validToken3);
        
        // Consume token
        tokenService.consumeToken(token);
        
        // Further validations should fail
        assertThrows(InvalidPasswordResetTokenException.class, () -> {
            tokenService.validateAndGetToken(token);
        });
    }
}