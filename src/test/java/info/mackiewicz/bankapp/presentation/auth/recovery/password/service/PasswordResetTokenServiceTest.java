package info.mackiewicz.bankapp.presentation.auth.recovery.password.service;

import info.mackiewicz.bankapp.presentation.auth.recovery.password.exception.ExpiredTokenException;
import info.mackiewicz.bankapp.presentation.auth.recovery.password.exception.TokenNotFoundException;
import info.mackiewicz.bankapp.presentation.auth.recovery.password.exception.TooManyPasswordResetAttemptsException;
import info.mackiewicz.bankapp.presentation.auth.recovery.password.exception.UsedTokenException;
import info.mackiewicz.bankapp.system.token.model.PasswordResetToken;
import info.mackiewicz.bankapp.system.token.repository.PasswordResetTokenRepository;
import info.mackiewicz.bankapp.system.token.service.TokenOperationsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetTokenServiceTest {

    @Mock
    private PasswordResetTokenRepository tokenRepository;
    
    @Mock
    private TokenOperationsService tokenHashingService;

    private PasswordResetTokenService tokenService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_TOKEN = "test-token";
    private static final String TEST_TOKEN_HASH = "test-token-hash";
    private static final String TEST_FULL_NAME = "Test User";

    @BeforeEach
    void setUp() {
        tokenService = new PasswordResetTokenService(tokenRepository, tokenHashingService);
    }

    @Test
    void createToken_WhenUserHasNoActiveTokens_ShouldCreateNewToken() {
        // given
        when(tokenRepository.countValidTokensByUserEmail(eq(TEST_EMAIL), any(LocalDateTime.class)))
                .thenReturn(0);
        when(tokenRepository.save(any(PasswordResetToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(tokenHashingService.generateToken()).thenReturn(TEST_TOKEN);
        when(tokenHashingService.hashToken(TEST_TOKEN)).thenReturn(TEST_TOKEN_HASH);

        // when
        String token = tokenService.createToken(TEST_EMAIL, TEST_FULL_NAME);

        // then
        assertNotNull(token);
        assertEquals(TEST_TOKEN, token);

        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepository).save(tokenCaptor.capture());

        PasswordResetToken savedToken = tokenCaptor.getValue();
        assertEquals(TEST_TOKEN_HASH, savedToken.getTokenHash());
        assertEquals(TEST_EMAIL, savedToken.getUserEmail());
        assertEquals(TEST_FULL_NAME, savedToken.getFullName());
        assertFalse(savedToken.isUsed());
        assertTrue(savedToken.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    @Test
    void createToken_WhenUserHasTooManyActiveTokens_ShouldThrowException() {
        // given
        when(tokenRepository.countValidTokensByUserEmail(eq(TEST_EMAIL), any(LocalDateTime.class)))
                .thenReturn(2);

        // when & then
        TooManyPasswordResetAttemptsException exception = assertThrows(TooManyPasswordResetAttemptsException.class,
                () -> tokenService.createToken(TEST_EMAIL, TEST_FULL_NAME));
        
        assertEquals("User exceeded token limit: " + TEST_EMAIL, exception.getMessage());
        verify(tokenRepository, never()).save(any());
        verify(tokenHashingService, never()).generateToken();
    }

    @Test
    void getValidatedToken_WhenTokenIsValid_ShouldReturnToken() {
        // given
        PasswordResetToken validToken = new PasswordResetToken(TEST_TOKEN_HASH, TEST_EMAIL, TEST_FULL_NAME);
        when(tokenHashingService.hashToken(TEST_TOKEN)).thenReturn(TEST_TOKEN_HASH);
        when(tokenRepository.findByTokenHash(TEST_TOKEN_HASH))
                .thenReturn(Optional.of(validToken));

        // when
        PasswordResetToken result = tokenService.getValidatedToken(TEST_TOKEN);

        // then
        assertNotNull(result);
        assertEquals(TEST_EMAIL, result.getUserEmail());
    }

    @Test
    void getValidatedToken_WhenTokenIsExpired_ShouldThrowException() {
        // given
        PasswordResetToken expiredToken = new PasswordResetToken(TEST_TOKEN_HASH, TEST_EMAIL, TEST_FULL_NAME);
        expiredToken.setExpiresAt(LocalDateTime.now().minusHours(1));
        
        when(tokenHashingService.hashToken(TEST_TOKEN)).thenReturn(TEST_TOKEN_HASH);
        when(tokenRepository.findByTokenHash(TEST_TOKEN_HASH))
                .thenReturn(Optional.of(expiredToken));

        // when/then
        ExpiredTokenException exception = assertThrows(ExpiredTokenException.class, 
            () -> tokenService.getValidatedToken(TEST_TOKEN)
        );
        assertEquals("Token has expired for user: " + TEST_EMAIL, exception.getMessage());
    }

    @Test
    void getValidatedToken_WhenTokenIsUsed_ShouldThrowException() {
        // given
        PasswordResetToken usedToken = new PasswordResetToken(TEST_TOKEN_HASH, TEST_EMAIL, TEST_FULL_NAME);
        usedToken.setUsed(true);
        usedToken.setUsedAt(LocalDateTime.now());
        
        when(tokenHashingService.hashToken(TEST_TOKEN)).thenReturn(TEST_TOKEN_HASH);
        when(tokenRepository.findByTokenHash(TEST_TOKEN_HASH))
                .thenReturn(Optional.of(usedToken));

        // when/then
        UsedTokenException exception = assertThrows(UsedTokenException.class,
            () -> tokenService.getValidatedToken(TEST_TOKEN)
        );
        assertEquals("Token has already been used for user: " + TEST_EMAIL, exception.getMessage());
    }

    @Test
    void consumeToken_WhenTokenIsValid_ShouldMarkAsUsed() {
        // given
        PasswordResetToken validToken = new PasswordResetToken(TEST_TOKEN_HASH, TEST_EMAIL, TEST_FULL_NAME);
        when(tokenRepository.save(any(PasswordResetToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        tokenService.consumeToken(validToken);

        // then
        verify(tokenRepository).save(argThat(token ->
            token.isUsed() && token.getUsedAt() != null
        ));
    }

    @Test
    void getValidatedToken_WhenTokenIsInvalid_ShouldThrowException() {
        // given
        when(tokenHashingService.hashToken(TEST_TOKEN)).thenReturn(TEST_TOKEN_HASH);
        when(tokenRepository.findByTokenHash(TEST_TOKEN_HASH))
                .thenReturn(Optional.empty());

        // when/then
        TokenNotFoundException exception = assertThrows(TokenNotFoundException.class,
            () -> tokenService.getValidatedToken(TEST_TOKEN)
        );
        assertEquals("Token not found", exception.getMessage());
    }

    @Test
    void canRequestNewToken_WhenUserHasNoActiveTokens_ShouldReturnTrue() {
        // given
        when(tokenRepository.countValidTokensByUserEmail(eq(TEST_EMAIL), any(LocalDateTime.class)))
                .thenReturn(0);

        // when
        boolean result = tokenService.canRequestNewToken(TEST_EMAIL);

        // then
        assertTrue(result);
    }

    @Test
    void canRequestNewToken_WhenUserHasTooManyActiveTokens_ShouldReturnFalse() {
        // given
        when(tokenRepository.countValidTokensByUserEmail(eq(TEST_EMAIL), any(LocalDateTime.class)))
                .thenReturn(2);

        // when
        boolean result = tokenService.canRequestNewToken(TEST_EMAIL);

        // then
        assertFalse(result);
    }
}