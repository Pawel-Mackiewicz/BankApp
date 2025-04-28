package info.mackiewicz.bankapp.system.recovery.password.service;

import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.core.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.core.user.service.UserService;
import info.mackiewicz.bankapp.system.notification.email.EmailService;
import info.mackiewicz.bankapp.system.notification.email.exception.EmailSendingException;
import info.mackiewicz.bankapp.system.recovery.password.controller.dto.PasswordChangeForm;
import info.mackiewicz.bankapp.system.recovery.password.exception.PasswordChangeException;
import info.mackiewicz.bankapp.system.recovery.password.exception.TokenCreationException;
import info.mackiewicz.bankapp.system.recovery.password.exception.UnexpectedTokenValidationException;
import info.mackiewicz.bankapp.system.token.model.PasswordResetToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void requestReset_WhenTokenCreationFails_ShouldThrowTokenCreationException() {
        // given
        User testUser = mock(User.class);
        when(testUser.getFullName()).thenReturn(TEST_FULL_NAME);
        when(userService.getUserByEmail(TEST_EMAIL)).thenReturn(testUser);
        when(passwordResetTokenService.createToken(TEST_EMAIL, TEST_FULL_NAME))
            .thenThrow(new RuntimeException("Database error"));

        // when/then
        assertThatThrownBy(() -> passwordResetService.requestReset(TEST_EMAIL))
            .isInstanceOf(TokenCreationException.class)
            .hasMessageContaining("Failed to create password reset token for email: " + TEST_EMAIL);

        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString(), anyString());
    }

    @Test
    void requestReset_WhenEmailSendingFails_ShouldThrowEmailSendingException() {
        // given
        User testUser = mock(User.class);
        when(testUser.getFullName()).thenReturn(TEST_FULL_NAME);
        when(userService.getUserByEmail(TEST_EMAIL)).thenReturn(testUser);
        when(passwordResetTokenService.createToken(TEST_EMAIL, TEST_FULL_NAME)).thenReturn(TEST_TOKEN);
        doThrow(new EmailSendingException("SMTP error"))
            .when(emailService).sendPasswordResetEmail(TEST_EMAIL, TEST_TOKEN, TEST_FULL_NAME);

        // when/then
        assertThatThrownBy(() -> passwordResetService.requestReset(TEST_EMAIL))
            .isInstanceOf(EmailSendingException.class)
            .hasMessageContaining("SMTP error");
    }

    @Test
    void completeReset_WhenTokenValidationFails_ShouldThrowTokenValidationException() {
        // given
        when(passwordResetTokenService.getValidatedToken(TEST_TOKEN))
            .thenThrow(new RuntimeException("Validation error"));

        // when/then
        assertThatThrownBy(() ->
            passwordResetService.completeReset(createResetDTO(TEST_TOKEN, NEW_PASSWORD, NEW_PASSWORD)))
            .isInstanceOf(UnexpectedTokenValidationException.class)
            .hasMessageContaining("Failed to validate password reset token");
    }

    @Test
    void completeReset_WhenPasswordUpdateFails_ShouldThrowPasswordChangeException() {
        // given
        PasswordResetToken mockToken = mock(PasswordResetToken.class);
        when(mockToken.getUserEmail()).thenReturn(TEST_EMAIL);
        when(mockToken.getFullName()).thenReturn(TEST_FULL_NAME);
        when(passwordResetTokenService.getValidatedToken(TEST_TOKEN)).thenReturn(mockToken);
        EmailAddress testEmail = new EmailAddress(TEST_EMAIL);
        doThrow(new RuntimeException("Password update failed"))
            .when(userService).changeUsersPassword(testEmail, NEW_PASSWORD);

        // when/then
        assertThatThrownBy(() ->
            passwordResetService.completeReset(createResetDTO(TEST_TOKEN, NEW_PASSWORD, NEW_PASSWORD)))
            .isInstanceOf(PasswordChangeException.class)
            .hasMessage("Failed to update password for email: " + TEST_EMAIL + "\nPassword update failed");

        verify(passwordResetTokenService).consumeToken(mockToken);
        verify(emailService, never()).sendPasswordResetConfirmation(anyString(), anyString());
    }

    @Test
    void completeReset_WhenConfirmationEmailFails_ShouldThrowEmailSendingException() {
        // given
        PasswordResetToken mockToken = mock(PasswordResetToken.class);
        when(mockToken.getUserEmail()).thenReturn(TEST_EMAIL);
        when(mockToken.getFullName()).thenReturn(TEST_FULL_NAME);
        when(passwordResetTokenService.getValidatedToken(TEST_TOKEN)).thenReturn(mockToken);
        
        doThrow(new EmailSendingException("Failed to send confirmation"))
            .when(emailService).sendPasswordResetConfirmation(TEST_EMAIL, TEST_FULL_NAME);

        // when/then
        assertThatThrownBy(() ->
            passwordResetService.completeReset(createResetDTO(TEST_TOKEN, NEW_PASSWORD, NEW_PASSWORD)))
            .isInstanceOf(EmailSendingException.class)
            .hasMessage("Failed to send confirmation");

        verify(passwordResetTokenService).consumeToken(mockToken);
        verify(userService).changeUsersPassword(new EmailAddress(TEST_EMAIL), NEW_PASSWORD);
    }

    @Test
    void completeReset_ShouldExecuteOperationsInCorrectOrder() {
        // given
        PasswordResetToken mockToken = mock(PasswordResetToken.class);
        when(mockToken.getUserEmail()).thenReturn(TEST_EMAIL);
        when(mockToken.getFullName()).thenReturn(TEST_FULL_NAME);
        when(passwordResetTokenService.getValidatedToken(TEST_TOKEN)).thenReturn(mockToken);
        
        var inOrder = inOrder(passwordResetTokenService, userService, emailService);

        // when
        passwordResetService.completeReset(createResetDTO(TEST_TOKEN, NEW_PASSWORD, NEW_PASSWORD));

        // then
        inOrder.verify(passwordResetTokenService).getValidatedToken(TEST_TOKEN);
        inOrder.verify(passwordResetTokenService).consumeToken(mockToken);
        inOrder.verify(userService).changeUsersPassword(new EmailAddress(TEST_EMAIL), NEW_PASSWORD);
        inOrder.verify(emailService).sendPasswordResetConfirmation(TEST_EMAIL, TEST_FULL_NAME);
        inOrder.verifyNoMoreInteractions();
    }

    private PasswordChangeForm createResetDTO(String token, String password, String confirmPassword) {
        PasswordChangeForm dto = new PasswordChangeForm();
        dto.setToken(token);
        dto.setPassword(password);
        dto.setConfirmPassword(confirmPassword);
        return dto;
    }
}