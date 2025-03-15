package info.mackiewicz.bankapp.security.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import info.mackiewicz.bankapp.security.exception.ExpiredPasswordResetTokenException;
import info.mackiewicz.bankapp.security.exception.TokenNotFoundException;
import info.mackiewicz.bankapp.security.exception.TooManyPasswordResetAttemptsException;
import info.mackiewicz.bankapp.security.exception.UsedPasswordResetTokenException;
import info.mackiewicz.bankapp.security.model.PasswordResetToken;
import info.mackiewicz.bankapp.security.repository.PasswordResetTokenRepository;

@ExtendWith(MockitoExtension.class)
class PasswordResetTokenServiceTest {

    @Mock
    private PasswordResetTokenRepository tokenRepository;
    
    @Mock
    private TokenHashingService tokenHashingService;

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
                .thenReturn(0L);
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
        .thenReturn(2L);

// when & then
assertThrows(TooManyPasswordResetAttemptsException.class,
        () -> tokenService.createToken(TEST_EMAIL, TEST_FULL_NAME));
verify(tokenRepository, never()).save(any());
verify(tokenHashingService, never()).generateToken();
    }

    @Test
    void validateAndGetToken_WhenTokenIsValid_ShouldReturnToken() {
        // given
        PasswordResetToken validToken = new PasswordResetToken(TEST_TOKEN_HASH, TEST_EMAIL, TEST_FULL_NAME);
        when(tokenHashingService.hashToken(TEST_TOKEN)).thenReturn(TEST_TOKEN_HASH);
        when(tokenRepository.findByTokenHash(TEST_TOKEN_HASH))
                .thenReturn(Optional.of(validToken));

        // when
        PasswordResetToken result = tokenService.validateAndRetrieveToken(TEST_TOKEN);

        // then
        assertNotNull(result);
        assertEquals(TEST_EMAIL, result.getUserEmail());
    }

    @Test
    void validateAndGetToken_WhenTokenIsExpired_ShouldThrowException() {
        // given
        PasswordResetToken expiredToken = new PasswordResetToken(TEST_TOKEN_HASH, TEST_EMAIL, TEST_FULL_NAME);
        expiredToken.setExpiresAt(LocalDateTime.now().minusHours(1));
        
        when(tokenHashingService.hashToken(TEST_TOKEN)).thenReturn(TEST_TOKEN_HASH);
        when(tokenRepository.findByTokenHash(TEST_TOKEN_HASH))
                .thenReturn(Optional.of(expiredToken));

        // when/then
        assertThrows(ExpiredPasswordResetTokenException.class, () ->
            tokenService.validateAndRetrieveToken(TEST_TOKEN)
        );
    }

    @Test
    void validateAndGetToken_WhenTokenIsUsed_ShouldThrowException() {
        // given
        PasswordResetToken usedToken = new PasswordResetToken(TEST_TOKEN_HASH, TEST_EMAIL, TEST_FULL_NAME);
        usedToken.setUsed(true);
        usedToken.setUsedAt(LocalDateTime.now());
        
        when(tokenHashingService.hashToken(TEST_TOKEN)).thenReturn(TEST_TOKEN_HASH);
        when(tokenRepository.findByTokenHash(TEST_TOKEN_HASH))
                .thenReturn(Optional.of(usedToken));

        // when/then
        assertThrows(UsedPasswordResetTokenException.class, () ->
            tokenService.validateAndRetrieveToken(TEST_TOKEN)
        );
    }

    @Test
    void consumeToken_WhenTokenIsValid_ShouldMarkAsUsed() {
        // given
        PasswordResetToken validToken = new PasswordResetToken(TEST_TOKEN_HASH, TEST_EMAIL, TEST_FULL_NAME);
        when(tokenHashingService.hashToken(TEST_TOKEN)).thenReturn(TEST_TOKEN_HASH);
        when(tokenRepository.findByTokenHash(TEST_TOKEN_HASH))
                .thenReturn(Optional.of(validToken));
        when(tokenRepository.save(any(PasswordResetToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        tokenService.consumeToken(TEST_TOKEN);

        // then
        verify(tokenRepository).save(argThat(token ->
            token.isUsed() && token.getUsedAt() != null
        ));
    }

    @Test
    void consumeToken_WhenTokenIsInvalid_ShouldThrowException() {
        // given
        when(tokenHashingService.hashToken(TEST_TOKEN)).thenReturn(TEST_TOKEN_HASH);
        when(tokenRepository.findByTokenHash(TEST_TOKEN_HASH))
                .thenReturn(Optional.empty());

        // when/then
        assertThrows(TokenNotFoundException.class, () ->
            tokenService.consumeToken(TEST_TOKEN)
        );
    }

    @Test
    void canRequestToken_WhenUserHasNoActiveTokens_ShouldReturnTrue() {
        // given
        when(tokenRepository.countValidTokensByUserEmail(eq(TEST_EMAIL), any(LocalDateTime.class)))
                .thenReturn(0L);

        // when
        boolean result = tokenService.canRequestToken(TEST_EMAIL);

        // then
        assertTrue(result);
    }

    @Test
    void canRequestToken_WhenUserHasTooManyActiveTokens_ShouldReturnFalse() {
        // given
        when(tokenRepository.countValidTokensByUserEmail(eq(TEST_EMAIL), any(LocalDateTime.class)))
                .thenReturn(2L);

        // when
        boolean result = tokenService.canRequestToken(TEST_EMAIL);

        // then
        assertFalse(result);
    }
}