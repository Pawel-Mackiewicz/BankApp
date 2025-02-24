package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.model.PasswordResetToken;
import info.mackiewicz.bankapp.repository.PasswordResetTokenRepository;
import info.mackiewicz.bankapp.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetTokenServiceTest {

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    private PasswordResetTokenService tokenService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_TOKEN = "test-token";

    @BeforeEach
    void setUp() {
        tokenService = new PasswordResetTokenService(tokenRepository);
    }

    @Test
    void createToken_ShouldGenerateNewToken_WhenUserHasNoActiveTokens() {
        // Arrange
        when(tokenRepository.countValidTokensByUserEmail(eq(TEST_EMAIL), any(LocalDateTime.class))).thenReturn(0L);
        when(tokenRepository.save(any(PasswordResetToken.class))).thenAnswer(i -> i.getArgument(0));

        try (MockedStatic<JwtUtil> jwtUtil = mockStatic(JwtUtil.class)) {
            jwtUtil.when(() -> JwtUtil.generateToken(TEST_EMAIL)).thenReturn(TEST_TOKEN);

            // Act
            String token = tokenService.createToken(TEST_EMAIL);

            // Assert
            assertEquals(TEST_TOKEN, token);
            verify(tokenRepository).save(any(PasswordResetToken.class));
            verify(tokenRepository).countValidTokensByUserEmail(eq(TEST_EMAIL), any(LocalDateTime.class));
            jwtUtil.verify(() -> JwtUtil.generateToken(TEST_EMAIL));
        }
    }

    @Test
    void createToken_ShouldThrowException_WhenUserExceededTokenLimit() {
        // Arrange
        when(tokenRepository.countValidTokensByUserEmail(eq(TEST_EMAIL), any(LocalDateTime.class))).thenReturn(2L);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> tokenService.createToken(TEST_EMAIL));
        verify(tokenRepository, never()).save(any(PasswordResetToken.class));
    }

    @Test
    void validateToken_ShouldReturnUserEmail_WhenTokenIsValid() {
        // Arrange
        PasswordResetToken validToken = new PasswordResetToken(TEST_TOKEN, TEST_EMAIL);
        when(tokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.of(validToken));

        // Act
        Optional<String> result = tokenService.validateToken(TEST_TOKEN);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(TEST_EMAIL, result.get());
    }

    @Test
    void validateToken_ShouldReturnEmpty_WhenTokenDoesNotExist() {
        // Arrange
        when(tokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.empty());

        // Act
        Optional<String> result = tokenService.validateToken(TEST_TOKEN);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void validateToken_ShouldReturnEmpty_WhenTokenIsExpired() {
        // Arrange
        PasswordResetToken expiredToken = new PasswordResetToken(TEST_TOKEN, TEST_EMAIL);
        expiredToken.setExpiresAt(LocalDateTime.now().minusHours(1));
        when(tokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.of(expiredToken));

        // Act
        Optional<String> result = tokenService.validateToken(TEST_TOKEN);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void consumeToken_ShouldMarkTokenAsUsed_WhenTokenIsValid() {
        // Arrange
        PasswordResetToken validToken = new PasswordResetToken(TEST_TOKEN, TEST_EMAIL);
        when(tokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.of(validToken));
        when(tokenRepository.save(any(PasswordResetToken.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        boolean result = tokenService.consumeToken(TEST_TOKEN);

        // Assert
        assertTrue(result);
        assertTrue(validToken.isUsed());
        assertNotNull(validToken.getUsedAt());
        verify(tokenRepository).save(validToken);
    }

    @Test
    void consumeToken_ShouldReturnFalse_WhenTokenDoesNotExist() {
        // Arrange
        when(tokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.empty());

        // Act
        boolean result = tokenService.consumeToken(TEST_TOKEN);

        // Assert
        assertFalse(result);
        verify(tokenRepository, never()).save(any());
    }

    @Test
    void consumeToken_ShouldReturnFalse_WhenTokenIsExpired() {
        // Arrange
        PasswordResetToken expiredToken = new PasswordResetToken(TEST_TOKEN, TEST_EMAIL);
        expiredToken.setExpiresAt(LocalDateTime.now().minusHours(1));
        when(tokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.of(expiredToken));

        // Act
        boolean result = tokenService.consumeToken(TEST_TOKEN);

        // Assert
        assertFalse(result);
        verify(tokenRepository, never()).save(any());
    }

    @Test
    void canRequestToken_ShouldReturnTrue_WhenUserHasNoActiveTokens() {
        // Arrange
        when(tokenRepository.countValidTokensByUserEmail(eq(TEST_EMAIL), any(LocalDateTime.class))).thenReturn(0L);

        // Act
        boolean result = tokenService.canRequestToken(TEST_EMAIL);

        // Assert
        assertTrue(result);
    }

    @Test
    void canRequestToken_ShouldReturnFalse_WhenUserHasMaxActiveTokens() {
        // Arrange
        when(tokenRepository.countValidTokensByUserEmail(eq(TEST_EMAIL), any(LocalDateTime.class))).thenReturn(2L);

        // Act
        boolean result = tokenService.canRequestToken(TEST_EMAIL);

        // Assert
        assertFalse(result);
    }
}