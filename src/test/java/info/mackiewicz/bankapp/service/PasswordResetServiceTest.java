package info.mackiewicz.bankapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordResetTokenService passwordResetTokenService;

    @Mock
    private EmailService emailService;

    private PasswordResetService passwordResetService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_TOKEN = "test-token";
    private static final String NEW_PASSWORD = "newPassword123";

    @BeforeEach
    void setUp() {
        passwordResetService = new PasswordResetService(
            userService,
            passwordResetTokenService,
            emailService
        );
    }

    @Test
    void requestReset_WhenUserExists_ShouldCreateTokenAndSendEmail() {
        // given
        when(userService.userExistsByEmail(TEST_EMAIL)).thenReturn(true);
        when(passwordResetTokenService.createToken(TEST_EMAIL)).thenReturn(TEST_TOKEN);

        // when
        passwordResetService.requestReset(TEST_EMAIL);

        // then
        verify(passwordResetTokenService).createToken(TEST_EMAIL);
        verify(emailService).sendPasswordResetEmail(TEST_EMAIL, TEST_TOKEN);
    }

    @Test
    void requestReset_WhenUserDoesNotExist_ShouldNotCreateTokenOrSendEmail() {
        // given
        when(userService.userExistsByEmail(TEST_EMAIL)).thenReturn(false);

        // when
        passwordResetService.requestReset(TEST_EMAIL);

        // then
        verify(passwordResetTokenService, never()).createToken(anyString());
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    @Test
    void completeReset_ShouldConsumeTokenUpdatePasswordAndSendConfirmation() {
        // when
        passwordResetService.completeReset(TEST_TOKEN, TEST_EMAIL, NEW_PASSWORD);

        // then
        verify(passwordResetTokenService).consumeToken(TEST_TOKEN);
        verify(userService).changeUsersPassword(TEST_EMAIL, NEW_PASSWORD);
        verify(emailService).sendPasswordResetConfirmation(TEST_EMAIL);
    }

    @Test
    void validateToken_WhenTokenValid_ShouldReturnEmail() {
        // given
        when(passwordResetTokenService.validateToken(TEST_TOKEN))
            .thenReturn(Optional.of(TEST_EMAIL));

        // when
        Optional<String> result = passwordResetService.validateToken(TEST_TOKEN);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(TEST_EMAIL);
        verify(passwordResetTokenService).validateToken(TEST_TOKEN);
    }

    @Test
    void validateToken_WhenTokenInvalid_ShouldReturnEmpty() {
        // given
        when(passwordResetTokenService.validateToken(TEST_TOKEN))
            .thenReturn(Optional.empty());

        // when
        Optional<String> result = passwordResetService.validateToken(TEST_TOKEN);

        // then
        assertThat(result).isEmpty();
        verify(passwordResetTokenService).validateToken(TEST_TOKEN);
    }

    @Test
    void completeReset_ShouldExecuteOperationsInCorrectOrder() {
        // given
        // Używamy inOrder do weryfikacji kolejności wywołań
        var inOrder = inOrder(passwordResetTokenService, userService, emailService);

        // when
        passwordResetService.completeReset(TEST_TOKEN, TEST_EMAIL, NEW_PASSWORD);

        // then
        inOrder.verify(passwordResetTokenService).consumeToken(TEST_TOKEN);
        inOrder.verify(userService).changeUsersPassword(TEST_EMAIL, NEW_PASSWORD);
        inOrder.verify(emailService).sendPasswordResetConfirmation(TEST_EMAIL);
        inOrder.verifyNoMoreInteractions();
    }
}