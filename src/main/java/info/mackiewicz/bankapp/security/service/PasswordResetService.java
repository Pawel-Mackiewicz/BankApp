package info.mackiewicz.bankapp.security.service;

import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.notification.email.EmailService;
import info.mackiewicz.bankapp.presentation.auth.dto.PasswordResetDTO;
import info.mackiewicz.bankapp.security.exception.ExpiredPasswordResetTokenException;
import info.mackiewicz.bankapp.security.exception.TokenNotFoundException;
import info.mackiewicz.bankapp.security.exception.TooManyPasswordResetAttemptsException;
import info.mackiewicz.bankapp.security.exception.UsedPasswordResetTokenException;
import info.mackiewicz.bankapp.security.model.PasswordResetToken;
import info.mackiewicz.bankapp.user.exception.UserNotFoundException;
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
     * @throws UserNotFoundException if user with given email doesn't exist
     */
    public void requestReset(String email) {

        log.info("Password reset requested for email: {}", email);

        try {
            var user = userService.getUserByEmail(email);
            String token = passwordResetTokenService.createToken(email, user.getFullName());
            emailService.sendPasswordResetEmail(email, token, user.getFullName());
        } catch (UserNotFoundException e) {
            // We don't want to expose the fact that the user doesn't exist for security reasons
            log.info("User with email {} not found", email);
            return;
        } catch (TooManyPasswordResetAttemptsException e) {
            log.warn("Too many password reset attempts for email: {}", email);
            throw e;
        } catch (Exception e) {
            log.error("An unexpected error occurred while processing the password reset request for email: {}", email, e);
        }

    }

    /**
     * Complete the password reset process by consuming the token and updating the password
     *
     * @param token Token to consume
     * @param email Email of the user
     * @param newPassword New password to set
     * @throws IllegalStateException if token is invalid or already used
     * @throws UserNotFoundException if user with given email doesn't exist
     * @throws ExpiredPasswordResetTokenException if token is expired
     * @throws UsedPasswordResetTokenException if token has already been used
     */
    @Transactional
    public void completeReset(PasswordResetDTO request) {
        
        PasswordResetToken token = validateAndRetrieveToken(request.getToken());
        String email = token.getUserEmail();
        String newPassword = request.getPassword();
        String fullNameOfUser = token.getFullName();

        passwordResetTokenService.consumeToken(token);
        
        log.debug("Token successfully consumed, updating password for email: {}", email);
        userService.changeUsersPassword(email, newPassword);
        
        log.debug("Password updated successfully, sending confirmation email to: {}", email);
        emailService.sendPasswordResetConfirmation(email, fullNameOfUser);
        
        log.info("Password reset completed successfully for email: {}", email);
    }

    /**
     * Validates a password reset token
     * exception is thrown if token is not found, expired or already used
     * @param token Token to validate
     * @return PasswordResetToken containing the user's email and other details if token is valid
     * @throws TokenNotFoundException            if token is not found
     * @throws ExpiredPasswordResetTokenException if token is expired
     * @throws UsedPasswordResetTokenException    if token has already been used
     */
    public PasswordResetToken validateAndRetrieveToken(String token) {
        PasswordResetToken validatedToken = passwordResetTokenService.validateAndRetrieveToken(token);
        log.debug("Token successfully validated and retrieved: {}", token);
        return validatedToken;
    }

}
