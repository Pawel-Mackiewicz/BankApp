package info.mackiewicz.bankapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import info.mackiewicz.bankapp.notification.email.EmailService;
import info.mackiewicz.bankapp.security.model.PasswordResetToken;
import info.mackiewicz.bankapp.security.service.PasswordResetService;
import info.mackiewicz.bankapp.security.service.PasswordResetTokenService;
import info.mackiewicz.bankapp.shared.exception.TooManyPasswordResetAttemptsException;
import info.mackiewicz.bankapp.shared.exception.UserNotFoundException;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    private static final String TEST_FULL_NAME = "Test User";
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
        User testUser = mock(User.class);
        when(testUser.getFullName()).thenReturn(TEST_FULL_NAME);
        when(userService.getUserByEmail(TEST_EMAIL)).thenReturn(testUser);
        when(passwordResetTokenService.createToken(TEST_EMAIL, TEST_FULL_NAME)).thenReturn(TEST_TOKEN);

        // when
        passwordResetService.requestReset(TEST_EMAIL);

        // then
        verify(passwordResetTokenService).createToken(TEST_EMAIL, TEST_FULL_NAME);
        verify(emailService).sendPasswordResetEmail(TEST_EMAIL, TEST_TOKEN, TEST_FULL_NAME);
        verifyNoMoreInteractions(emailService, passwordResetTokenService);
    }

    @Test
    void requestReset_WhenUserDoesNotExist_ShouldHandleGracefully() {
        // given
        when(userService.getUserByEmail(TEST_EMAIL))
            .thenThrow(new UserNotFoundException("User not found"));

        // when
        passwordResetService.requestReset(TEST_EMAIL);

        // then
        verify(passwordResetTokenService, never()).createToken(anyString(), anyString());
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString(), anyString());
    }

    @Test
    void requestReset_WhenTooManyAttempts_ShouldNotSendEmail() {
        // given
        User testUser = mock(User.class);
        when(testUser.getFullName()).thenReturn(TEST_FULL_NAME);
        when(userService.getUserByEmail(TEST_EMAIL)).thenReturn(testUser);
        when(passwordResetTokenService.createToken(TEST_EMAIL, TEST_FULL_NAME))
            .thenThrow(new TooManyPasswordResetAttemptsException());

        // when/then
        assertThatThrownBy(() -> passwordResetService.requestReset(TEST_EMAIL))
            .isInstanceOf(TooManyPasswordResetAttemptsException.class);

        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString(), anyString());
    }

    @Test
    void completeReset_WhenTokenConsumptionFails_ShouldThrowException() {
        // given
        when(passwordResetTokenService.consumeToken(TEST_TOKEN)).thenReturn(false);

        // when/then
        assertThatThrownBy(() ->
            passwordResetService.completeReset(TEST_TOKEN, TEST_EMAIL, TEST_FULL_NAME, NEW_PASSWORD))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Token is invalid or already used");

        verify(userService, never()).changeUsersPassword(anyString(), anyString());
        verify(emailService, never()).sendPasswordResetConfirmation(anyString(), anyString());
    }

    @Test
    void completeReset_WhenPasswordUpdateFails_ShouldNotSendConfirmation() {
        // given
        when(passwordResetTokenService.consumeToken(TEST_TOKEN)).thenReturn(true);
        doThrow(new RuntimeException("Password update failed"))
            .when(userService).changeUsersPassword(TEST_EMAIL, NEW_PASSWORD);

        // when/then
        assertThatThrownBy(() ->
            passwordResetService.completeReset(TEST_TOKEN, TEST_EMAIL, TEST_FULL_NAME, NEW_PASSWORD))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Password update failed");

        verify(passwordResetTokenService).consumeToken(TEST_TOKEN);
        verify(emailService, never()).sendPasswordResetConfirmation(anyString(), anyString());
    }

    @Test
    void completeReset_ShouldConsumeTokenUpdatePasswordAndSendConfirmation() {
        // given
        when(passwordResetTokenService.consumeToken(TEST_TOKEN)).thenReturn(true);

        // when
        passwordResetService.completeReset(TEST_TOKEN, TEST_EMAIL, TEST_FULL_NAME, NEW_PASSWORD);

        // then
        verify(passwordResetTokenService).consumeToken(TEST_TOKEN);
        verify(userService).changeUsersPassword(TEST_EMAIL, NEW_PASSWORD);
        verify(emailService).sendPasswordResetConfirmation(TEST_EMAIL, TEST_FULL_NAME);
    }

    @Test
    void validateToken_WhenTokenValid_ShouldReturnEmail() {
        // given
        PasswordResetToken mockToken = mock(PasswordResetToken.class);
        when(mockToken.getUserEmail()).thenReturn(TEST_EMAIL);
        when(passwordResetTokenService.validateToken(TEST_TOKEN))
            .thenReturn(Optional.of(mockToken));

        // when
        Optional<PasswordResetToken> result = passwordResetService.validateToken(TEST_TOKEN);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUserEmail()).isEqualTo(TEST_EMAIL);
        verify(passwordResetTokenService).validateToken(TEST_TOKEN);
    }

    @Test
    void validateToken_WhenTokenInvalid_ShouldReturnEmpty() {
        // given
        when(passwordResetTokenService.validateToken(TEST_TOKEN))
            .thenReturn(Optional.empty());

        // when
        Optional<PasswordResetToken> result = passwordResetService.validateToken(TEST_TOKEN);

        // then
        assertThat(result).isEmpty();
        verify(passwordResetTokenService).validateToken(TEST_TOKEN);
    }

    @Test
    void completeReset_ShouldExecuteOperationsInCorrectOrder() {
        // given
        when(passwordResetTokenService.consumeToken(TEST_TOKEN)).thenReturn(true);
        // Using inOrder to verify the sequence of operations
        var inOrder = inOrder(passwordResetTokenService, userService, emailService);

        // when
        passwordResetService.completeReset(TEST_TOKEN, TEST_EMAIL, TEST_FULL_NAME, NEW_PASSWORD);

        // then
        inOrder.verify(passwordResetTokenService).consumeToken(TEST_TOKEN);
        inOrder.verify(userService).changeUsersPassword(TEST_EMAIL, NEW_PASSWORD);
        inOrder.verify(emailService).sendPasswordResetConfirmation(TEST_EMAIL, TEST_FULL_NAME);
        inOrder.verifyNoMoreInteractions();
    }
}