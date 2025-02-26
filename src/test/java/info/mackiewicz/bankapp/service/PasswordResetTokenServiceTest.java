package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.model.PasswordResetToken;
import info.mackiewicz.bankapp.repository.PasswordResetTokenRepository;
import info.mackiewicz.bankapp.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    void createToken_WhenUserHasNoActiveTokens_ShouldCreateNewToken() {
        // given
        when(tokenRepository.countValidTokensByUserEmail(eq(TEST_EMAIL), any(LocalDateTime.class)))
                .thenReturn(0L);
        when(tokenRepository.save(any(PasswordResetToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<JwtUtil> jwtUtilMock = Mockito.mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.generateToken(TEST_EMAIL))
                    .thenReturn(TEST_TOKEN);

            // when
            String token = tokenService.createToken(TEST_EMAIL);

            // then
            assertNotNull(token);
            assertEquals(TEST_TOKEN, token);

            ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
            verify(tokenRepository).save(tokenCaptor.capture());

            PasswordResetToken savedToken = tokenCaptor.getValue();
            assertEquals(TEST_TOKEN, savedToken.getToken());
            assertEquals(TEST_EMAIL, savedToken.getUserEmail());
            assertFalse(savedToken.isUsed());
            assertTrue(savedToken.getExpiresAt().isAfter(LocalDateTime.now()));
        }
    }

    @Test
    void createToken_WhenUserHasTooManyActiveTokens_ShouldThrowException() {
        // given
        when(tokenRepository.countValidTokensByUserEmail(eq(TEST_EMAIL), any(LocalDateTime.class)))
                .thenReturn(2L);

        // when & then
        assertThrows(IllegalStateException.class,
                () -> tokenService.createToken(TEST_EMAIL));
        verify(tokenRepository, never()).save(any());
    }

    @Test
    void validateToken_WhenTokenIsValid_ShouldReturnUserEmail() {
        // given
        PasswordResetToken validToken = new PasswordResetToken(TEST_TOKEN, TEST_EMAIL);
        when(tokenRepository.findByToken(TEST_TOKEN))
                .thenReturn(Optional.of(validToken));

        // when
        Optional<String> result = tokenService.validateToken(TEST_TOKEN);

        // then
        assertTrue(result.isPresent());
        assertEquals(TEST_EMAIL, result.get());
    }

    @Test
    void validateToken_WhenTokenIsExpired_ShouldReturnEmpty() {
        // given
        PasswordResetToken expiredToken = new PasswordResetToken(TEST_TOKEN, TEST_EMAIL);
        // Ustawiamy datę wygaśnięcia w przeszłości
        expiredToken.setExpiresAt(LocalDateTime.now().minusHours(1));
        
        when(tokenRepository.findByToken(TEST_TOKEN))
                .thenReturn(Optional.of(expiredToken));

        // when
        Optional<String> result = tokenService.validateToken(TEST_TOKEN);

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    void validateToken_WhenTokenIsUsed_ShouldReturnEmpty() {
        // given
        PasswordResetToken usedToken = new PasswordResetToken(TEST_TOKEN, TEST_EMAIL);
        usedToken.setUsed(true);
        usedToken.setUsedAt(LocalDateTime.now());
        
        when(tokenRepository.findByToken(TEST_TOKEN))
                .thenReturn(Optional.of(usedToken));

        // when
        Optional<String> result = tokenService.validateToken(TEST_TOKEN);

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    void consumeToken_WhenTokenIsValid_ShouldMarkAsUsedAndReturnTrue() {
        // given
        PasswordResetToken validToken = new PasswordResetToken(TEST_TOKEN, TEST_EMAIL);
        when(tokenRepository.findByToken(TEST_TOKEN))
                .thenReturn(Optional.of(validToken));
        when(tokenRepository.save(any(PasswordResetToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        boolean result = tokenService.consumeToken(TEST_TOKEN);

        // then
        assertTrue(result);
        verify(tokenRepository).save(argThat(token -> 
            token.isUsed() && token.getUsedAt() != null
        ));
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