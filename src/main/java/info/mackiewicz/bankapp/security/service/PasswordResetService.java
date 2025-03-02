package info.mackiewicz.bankapp.security.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.notification.email.EmailService;
import info.mackiewicz.bankapp.security.model.PasswordResetToken;
import info.mackiewicz.bankapp.shared.exception.UserNotFoundException;
import info.mackiewicz.bankapp.user.service.UserService;
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
        String fullNameOfUser;

        try {
            fullNameOfUser = userService.getUserByEmail(email).getFullName();
        } catch (UserNotFoundException e) {
            log.info("User with email {} not found", email);
            return;
        }
        String token = passwordResetTokenService.createToken(email, fullNameOfUser);
        emailService.sendPasswordResetEmail(email, token, fullNameOfUser);

    }

    /**
     * Complete the password reset process by consuming the token and updating the password
     *
     * @param token Token to consume
     * @param email Email of the user
     * @param newPassword New password to set
     * @throws IllegalStateException if token is invalid or already used
     */
    public void completeReset(String token, String email, String fullNameOfUser, String newPassword) {
        log.info("Attempting to complete password reset for email: {}", email);
        
        boolean tokenConsumed = passwordResetTokenService.consumeToken(token);
        if (!tokenConsumed) {
            log.warn("Password reset failed - invalid or already used token for email: {}", email);
            throw new IllegalStateException("Token is invalid or already used");
        }
        
        log.debug("Token successfully consumed, updating password for email: {}", email);
        userService.changeUsersPassword(email, newPassword);
        
        log.debug("Password updated successfully, sending confirmation email to: {}", email);
        emailService.sendPasswordResetConfirmation(email, fullNameOfUser);
        
        log.info("Password reset completed successfully for email: {}", email);
    }

    /**
     * Validates a token and returns associated user's email if valid
     * 
     * @param token Token to validate
     * @return Optional containing the user's email if token is valid, empty
     *         otherwise
     */
    public Optional<PasswordResetToken> validateToken(String token) {
        return passwordResetTokenService.validateToken(token);
    }

}
