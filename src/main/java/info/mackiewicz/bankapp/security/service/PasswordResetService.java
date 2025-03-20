package info.mackiewicz.bankapp.security.service;

import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.notification.email.EmailService;
import info.mackiewicz.bankapp.notification.email.exception.EmailSendingException;
import info.mackiewicz.bankapp.presentation.auth.dto.PasswordResetDTO;
import info.mackiewicz.bankapp.security.exception.ExpiredPasswordResetTokenException;
import info.mackiewicz.bankapp.security.exception.TokenNotFoundException;
import info.mackiewicz.bankapp.security.exception.TooManyPasswordResetAttemptsException;
import info.mackiewicz.bankapp.security.exception.UsedPasswordResetTokenException;
import info.mackiewicz.bankapp.security.model.PasswordResetToken;
import info.mackiewicz.bankapp.user.exception.InvalidEmailFormatException;
import info.mackiewicz.bankapp.user.exception.UserNotFoundException;
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
     * @throws InvalidEmailFormatException           if email is not valid
     * @throws EmailSendingException                 if email sending fails
     * @throws TooManyPasswordResetAttemptsException if too many password reset
     *                                               attempts have been made
     */
    public void requestReset(String email) {
        log.info("Initiating password reset process for email: {}", email);
        log.debug("Validating email format and checking user existence");

        try {
            var user = userService.getUserByEmail(email);
            log.debug("User found, generating reset token for user ID: {}", user.getId());
            
            String token = passwordResetTokenService.createToken(email, user.getFullName());
            log.debug("Reset token generated, sending email notification");
            
            emailService.sendPasswordResetEmail(email, token, user.getFullName());
            log.info("Password reset email successfully sent to: {}", email);
            
        } catch (UserNotFoundException e) {
            // We don't want to expose the fact that the user doesn't exist for security reasons
            log.info("Password reset requested for non-existent user: {}", email);
            return;
        } catch (TooManyPasswordResetAttemptsException e) {
            log.warn("Password reset rate limit exceeded for email: {}", email);
            throw e;
        } catch (Exception e) {
            log.error("Critical error during password reset process for email: {}", email, e);
            throw e;
        }
    }

    /**
     * Complete the password reset process by consuming the token and updating the
     * password
     *
     * @param token       Token to consume
     * @param email       Email of the user
     * @param newPassword New password to set
     * @throws IllegalStateException              if token is invalid or already
     *                                            used
     * @throws UserNotFoundException              if user with given email doesn't
     *                                            exist
     * @throws ExpiredPasswordResetTokenException if token is expired
     * @throws UsedPasswordResetTokenException    if token has already been used
     * @throws InvalidEmailFormatException        if email is not valid
     */
    @Transactional
    public void completeReset(PasswordResetDTO request) {
        String tokenId = request.getToken().substring(0, Math.min(8, request.getToken().length()));
        log.info("Starting password reset completion process for token ID: {}", tokenId);
        log.debug("Validating reset token: {}", tokenId);

        try {
            PasswordResetToken token = validateAndRetrieveToken(request.getToken());
            Email email = new Email(token.getUserEmail());
            String newPassword = request.getPassword();
            String fullNameOfUser = token.getFullName();

            log.debug("Token validated successfully. Processing reset for user: {}", email);
            
            passwordResetTokenService.consumeToken(token);
            log.debug("Token marked as consumed for user: {}", email);

            log.debug("Initiating password update for user: {}", email);
            userService.changeUsersPassword(email, newPassword);
            log.debug("Password successfully updated for user: {}", email);

            sendConfirmationEmail(email, fullNameOfUser);
            
            log.info("Password reset completed successfully for user: {} (token: {})", email, tokenId);
        } catch (Exception e) {
            log.error("Failed to complete password reset for token: {}. Error: {}", tokenId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Validates a password reset token
     * exception is thrown if token is not found, expired or already used
     * 
     * @param token Token to validate
     * @return PasswordResetToken containing the user's email and other details if
     *         token is valid
     * @throws TokenNotFoundException             if token is not found
     * @throws ExpiredPasswordResetTokenException if token is expired
     * @throws UsedPasswordResetTokenException    if token has already been used
     */
    public PasswordResetToken validateAndRetrieveToken(String token) {
        log.debug("Starting token validation process");
        try {
            PasswordResetToken validatedToken = passwordResetTokenService.getValidatedToken(token);
            log.debug("Token successfully validated for user: {}", validatedToken.getUserEmail());
            return validatedToken;
        } catch (TokenNotFoundException e) {
            log.warn("Password reset attempt with invalid token");
            throw e;
        } catch (ExpiredPasswordResetTokenException | UsedPasswordResetTokenException e) {
            log.warn("Password reset attempt with {}", e.getMessage().toLowerCase());
            throw e;
        }
    }

    /**
     * Sends a confirmation email to the user after password reset
     * 
     * @param email          Email of the user
     * @param fullNameOfUser Full name of the user
     * @throws InvalidEmailFormatException if email is not valid
     * @throws EmailSendingException       if email sending fails
     */
    private void sendConfirmationEmail(Email email, String fullNameOfUser) {
        log.debug("Initiating password reset confirmation email for user: {}", email);
        try {
            emailService.sendPasswordResetConfirmation(email.toString(), fullNameOfUser);
            log.debug("Password reset confirmation email sent successfully to: {}", email);
        } catch (EmailSendingException e) {
            log.error("Failed to send password reset confirmation email to: {}. Error: {}", email, e.getMessage(), e);
            throw e;
        }
    }
}
