package info.mackiewicz.bankapp.security.service;

import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.notification.email.EmailService;
import info.mackiewicz.bankapp.notification.email.exception.EmailSendingException;
import info.mackiewicz.bankapp.presentation.auth.dto.PasswordResetDTO;
import info.mackiewicz.bankapp.security.exception.ExpiredPasswordResetTokenException;
import info.mackiewicz.bankapp.security.exception.PasswordChangeException;
import info.mackiewicz.bankapp.security.exception.TokenCreationException;
import info.mackiewicz.bankapp.security.exception.TokenNotFoundException;
import info.mackiewicz.bankapp.security.exception.TokenValidationException;
import info.mackiewicz.bankapp.security.exception.TooManyPasswordResetAttemptsException;
import info.mackiewicz.bankapp.security.exception.UsedPasswordResetTokenException;
import info.mackiewicz.bankapp.security.model.PasswordResetToken;
import info.mackiewicz.bankapp.user.exception.InvalidEmailFormatException;
import info.mackiewicz.bankapp.user.exception.UserNotFoundException;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.vo.Email;
import info.mackiewicz.bankapp.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PasswordResetService {

    private final UserService userService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final EmailService emailService;

    /**
     * Initiates password reset process for user with given email
     * 
     * @param email Email of user requesting password reset
     * @throws TokenCreationException                if token creation fails
     * @throws TooManyPasswordResetAttemptsException if user has exceeded token
     *                                               limit
     * @throws UserNotFoundException                 if user with given email is not
     *                                               found
     * @throws InvalidEmailFormatException           if email is not valid
     * @throws EmailSendingException                 if email sending fails
     */
    public void requestReset(String email) {
        log.info("Initiating password reset process for email: {}", email);
        log.debug("Validating email format and checking user existence");

        var user = userService.getUserByEmail(email);
        log.debug("User found, generating reset token for user ID: {}", user.getId());

        String token = generatePasswordResetToken(email, user);

        sendPasswordResetEmailNotification(email, user, token);
    }

    private void sendPasswordResetEmailNotification(String email, User user, String token) {
        try {
            emailService.sendPasswordResetEmail(email, token, user.getFullName());
            log.info("Password reset email successfully sent to: {}", email);
        } catch (EmailSendingException e) {
            throw e;
        } catch (Exception e) {
            throw new EmailSendingException("Failed to send password reset email to: " + email + "\n" + e.getMessage(),
                    e);
        }
    }

    private String generatePasswordResetToken(String email, User user) {
        String token;
        try {
            token = passwordResetTokenService.createToken(email, user.getFullName());
            log.debug("Reset token generated, sending email notification");
        } catch (TooManyPasswordResetAttemptsException e) {
            throw e;
        } catch (Exception e) {
            throw new TokenCreationException(
                    "Failed to create password reset token for email: " + email + "\n" + e.getMessage(), e);
        }
        return token;
    }

    /**
     * Complete the password reset process by consuming the token and updating the
     * password
     *
     * @param request Password reset request containing token and new password
     * 
     * @throws ExpiredPasswordResetTokenException if token is expired
     * @throws UsedPasswordResetTokenException    if token is already used
     * @throws TokenValidationException           if token validation fails
     * @throws PasswordChangeException            if password update fails
     * @throws InvalidEmailFormatException        if email is not valid
     * @throws EmailSendingException              if email sending fails
     */
    @Transactional
    public void completeReset(PasswordResetDTO request) {
        String tokenId = request.getToken().substring(0, Math.min(8, request.getToken().length()));
        log.info("Starting password reset completion process for token ID: {}", tokenId);

        log.debug("Validating reset token: {}", tokenId);
        PasswordResetToken token = validateAndRetrieveToken(request.getToken());
        Email email = new Email(token.getUserEmail());
        String newPassword = request.getPassword();
        String fullNameOfUser = token.getFullName();
        log.debug("Token validated successfully.");

        log.debug("Processing password reset for user: {}", email);
        passwordResetTokenService.consumeToken(token);
        log.debug("Token marked as consumed for user: {}", email);

        log.debug("Initiating password update for user: {}", email);
        updateUserPassword(email, newPassword);
        log.debug("Password successfully updated for user: {}", email);

        log.debug("Sending password reset confirmation email to: {}", email);
        emailService.sendPasswordResetConfirmation(email.toString(), fullNameOfUser);
        log.info("Password reset completed successfully for user: {} (token: {})", email, tokenId);
    }

    private void updateUserPassword(Email email, String newPassword) {
        try {
            userService.changeUsersPassword(email, newPassword);
        } catch (Exception e) {
            throw new PasswordChangeException("Failed to update password for email: " + email + "\n" + e.getMessage(),
                    e);
        }
    }

    /**
     * Validates a password reset token
     * exception is thrown if token is not found, expired or already used
     * 
     * @param token Token to validate
     * @return PasswordResetToken containing the user's email and other details if
     *         token is valid
     * @throws TokenValidationException if token is invalid or not found
     */
    public PasswordResetToken validateAndRetrieveToken(String token) {
        PasswordResetToken validatedToken = null;
        try {
            validatedToken = passwordResetTokenService.getValidatedToken(token);
            log.debug("Token successfully validated for user: {}", validatedToken.getUserEmail());
            return validatedToken;
        } catch (ExpiredPasswordResetTokenException | UsedPasswordResetTokenException | TokenNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new TokenValidationException("Failed to validate password reset token for email: " +
                    (validatedToken != null ? validatedToken.getUserEmail() : "unknown") + "\n" + e.getMessage(), e);
        }
    }
}
