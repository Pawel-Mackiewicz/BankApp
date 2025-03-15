package info.mackiewicz.bankapp.security.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import info.mackiewicz.bankapp.notification.email.EmailService;
import info.mackiewicz.bankapp.presentation.auth.dto.PasswordResetDTO;
import info.mackiewicz.bankapp.security.exception.InvalidPasswordResetTokenException;
import info.mackiewicz.bankapp.security.exception.TooManyPasswordResetAttemptsException;
import info.mackiewicz.bankapp.security.model.PasswordResetToken;
import info.mackiewicz.bankapp.user.exception.UserNotFoundException;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.service.UserService;

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

    private PasswordResetDTO createResetDTO(String token, String password, String confirmPassword) {
        PasswordResetDTO dto = new PasswordResetDTO();
        dto.setToken(token);
        dto.setPassword(password);
        dto.setConfirmPassword(confirmPassword);
        return dto;
    }

    @Test
    void completeReset_WhenTokenConsumptionFails_ShouldThrowException() {
        // given
        PasswordResetToken mockToken = mock(PasswordResetToken.class);
        when(mockToken.getUserEmail()).thenReturn(TEST_EMAIL);
        when(mockToken.getFullName()).thenReturn(TEST_FULL_NAME);
        when(passwordResetTokenService.validateAndGetToken(TEST_TOKEN)).thenReturn(mockToken);
        doThrow(new InvalidPasswordResetTokenException("Token is invalid or already used"))
            .when(passwordResetTokenService).consumeToken(TEST_TOKEN);

        // when/then
        assertThatThrownBy(() ->
            passwordResetService.completeReset(createResetDTO(TEST_TOKEN, NEW_PASSWORD, NEW_PASSWORD)))
            .isInstanceOf(InvalidPasswordResetTokenException.class)
            .hasMessage("Token is invalid or already used");

        verify(userService, never()).changeUsersPassword(anyString(), anyString());
        verify(emailService, never()).sendPasswordResetConfirmation(anyString(), anyString());
    }

    @Test
    void completeReset_WhenPasswordUpdateFails_ShouldNotSendConfirmation() {
        // given
        PasswordResetToken mockToken = mock(PasswordResetToken.class);
        when(mockToken.getUserEmail()).thenReturn(TEST_EMAIL);
        when(mockToken.getFullName()).thenReturn(TEST_FULL_NAME);
        when(passwordResetTokenService.validateAndGetToken(TEST_TOKEN)).thenReturn(mockToken);
        doThrow(new RuntimeException("Password update failed"))
            .when(userService).changeUsersPassword(TEST_EMAIL, NEW_PASSWORD);

        // when/then
        assertThatThrownBy(() ->
            passwordResetService.completeReset(createResetDTO(TEST_TOKEN, NEW_PASSWORD, NEW_PASSWORD)))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Password update failed");

        verify(passwordResetTokenService).consumeToken(TEST_TOKEN);
        verify(emailService, never()).sendPasswordResetConfirmation(anyString(), anyString());
    }

    @Test
    void completeReset_ShouldConsumeTokenUpdatePasswordAndSendConfirmation() {
        // given
        PasswordResetToken mockToken = mock(PasswordResetToken.class);
        when(mockToken.getUserEmail()).thenReturn(TEST_EMAIL);
        when(mockToken.getFullName()).thenReturn(TEST_FULL_NAME);
        when(passwordResetTokenService.validateAndGetToken(TEST_TOKEN)).thenReturn(mockToken);

        // when
        passwordResetService.completeReset(createResetDTO(TEST_TOKEN, NEW_PASSWORD, NEW_PASSWORD));

        // then
        verify(passwordResetTokenService).consumeToken(TEST_TOKEN);
        verify(userService).changeUsersPassword(TEST_EMAIL, NEW_PASSWORD);
        verify(emailService).sendPasswordResetConfirmation(TEST_EMAIL, TEST_FULL_NAME);
    }

    @Test
    void validateToken_WhenTokenValid_ShouldReturnToken() {
        // given
        PasswordResetToken mockToken = mock(PasswordResetToken.class);
        when(mockToken.getUserEmail()).thenReturn(TEST_EMAIL);
        when(passwordResetTokenService.validateAndGetToken(TEST_TOKEN)).thenReturn(mockToken);

        // when
        PasswordResetToken result = passwordResetService.validateToken(TEST_TOKEN);

        // then
        assertThat(result.getUserEmail()).isEqualTo(TEST_EMAIL);
        verify(passwordResetTokenService).validateAndGetToken(TEST_TOKEN);
    }

    @Test
    void validateToken_WhenTokenInvalid_ShouldThrowException() {
        // given
        when(passwordResetTokenService.validateAndGetToken(TEST_TOKEN))
            .thenThrow(new InvalidPasswordResetTokenException("Invalid token provided"));

        // when/then
        assertThatThrownBy(() -> passwordResetService.validateToken(TEST_TOKEN))
            .isInstanceOf(InvalidPasswordResetTokenException.class)
            .hasMessage("Invalid token provided");

        verify(passwordResetTokenService).validateAndGetToken(TEST_TOKEN);
    }

    @Test
    void completeReset_ShouldExecuteOperationsInCorrectOrder() {
        // given
        PasswordResetToken mockToken = mock(PasswordResetToken.class);
        when(mockToken.getUserEmail()).thenReturn(TEST_EMAIL);
        when(mockToken.getFullName()).thenReturn(TEST_FULL_NAME);
        when(passwordResetTokenService.validateAndGetToken(TEST_TOKEN)).thenReturn(mockToken);
        
        var inOrder = inOrder(passwordResetTokenService, userService, emailService);

        // when
        passwordResetService.completeReset(createResetDTO(TEST_TOKEN, NEW_PASSWORD, NEW_PASSWORD));

        // then
        inOrder.verify(passwordResetTokenService).validateAndGetToken(TEST_TOKEN);
        inOrder.verify(passwordResetTokenService).consumeToken(TEST_TOKEN);
        inOrder.verify(userService).changeUsersPassword(TEST_EMAIL, NEW_PASSWORD);
        inOrder.verify(emailService).sendPasswordResetConfirmation(TEST_EMAIL, TEST_FULL_NAME);
        inOrder.verifyNoMoreInteractions();
    }
}