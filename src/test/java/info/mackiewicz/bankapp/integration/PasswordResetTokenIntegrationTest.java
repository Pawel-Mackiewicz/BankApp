package info.mackiewicz.bankapp.integration;

import info.mackiewicz.bankapp.config.TestConfig;
import info.mackiewicz.bankapp.model.PasswordResetToken;
import info.mackiewicz.bankapp.repository.PasswordResetTokenRepository;
import info.mackiewicz.bankapp.service.PasswordResetService;
import info.mackiewicz.bankapp.service.PasswordResetTokenService;
import info.mackiewicz.bankapp.service.TokenHashingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Import(TestConfig.class)
class PasswordResetTokenIntegrationTest {

    @Autowired
    private PasswordResetTokenService tokenService;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private TokenHashingService tokenHashingService;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    private static final String TEST_EMAIL = "test@example.com";

    @Test
    void fullPasswordResetFlow_ShouldWorkCorrectly() {
        // Arrange - Request password reset
        String token = tokenService.createToken(TEST_EMAIL);
        assertNotNull(token);

        // Verify token is stored as hash
        Optional<PasswordResetToken> storedToken = tokenRepository.findAll().stream()
                .filter(t -> t.getUserEmail().equals(TEST_EMAIL))
                .findFirst();
        assertTrue(storedToken.isPresent());
        assertNotEquals(token, storedToken.get().getTokenHash());

        // Act - Validate token
        Optional<String> validatedEmail = tokenService.validateToken(token);
        
        // Assert - Token validation successful
        assertTrue(validatedEmail.isPresent());
        assertEquals(TEST_EMAIL, validatedEmail.get());

        // Act - Consume token
        boolean consumed = tokenService.consumeToken(token);
        
        // Assert - Token consumed successfully
        assertTrue(consumed);
        
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
        String token1 = tokenService.createToken(TEST_EMAIL);
        String token2 = tokenService.createToken(TEST_EMAIL);
        
        // Assert both tokens are valid
        assertNotNull(token1);
        assertNotNull(token2);
        
        // Try to create one more token
        assertThrows(IllegalStateException.class, () -> {
            tokenService.createToken(TEST_EMAIL);
        });
    }

    @Test
    void expiredTokens_ShouldBeCleanedUp() {
        // Create a token
        String token = tokenService.createToken(TEST_EMAIL);
        
        // Manually expire the token in database
        tokenRepository.findAll().stream()
                .filter(t -> t.getUserEmail().equals(TEST_EMAIL))
                .findFirst()
                .ifPresent(t -> {
                    t.setExpiresAt(LocalDateTime.now().minusHours(1));
                    tokenRepository.save(t);
                });
        
        // Verify token is now invalid
        Optional<String> validatedEmail = tokenService.validateToken(token);
        assertFalse(validatedEmail.isPresent());
        
        // Clean up old tokens
        int deletedCount = tokenService.cleanupOldTokens(0);
        assertTrue(deletedCount > 0);
        
        // Verify token was deleted
        assertEquals(0, tokenRepository.findAll().size());
    }

    @Test
    void multipleValidations_ShouldWork() {
        // Create token
        String token = tokenService.createToken(TEST_EMAIL);
        
        // Multiple validations should work until consumed
        assertTrue(tokenService.validateToken(token).isPresent());
        assertTrue(tokenService.validateToken(token).isPresent());
        assertTrue(tokenService.validateToken(token).isPresent());
        
        // Consume token
        assertTrue(tokenService.consumeToken(token));
        
        // Further validations should fail
        assertFalse(tokenService.validateToken(token).isPresent());
    }
}