package info.mackiewicz.bankapp.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PasswordResetService {

    private final UserService userService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final EmailService emailService;

    public void requestReset(String email) {

        if (userService.userExistsByEmail(email)) {
            String token = passwordResetTokenService.createToken(email);
            emailService.sendPasswordResetEmail(email, token);
        }

    }

    // User get password reset form only after confirming token validity
    public void completeReset(String token, String email, String newPassword) {
        passwordResetTokenService.consumeToken(token);
        userService.changeUsersPassword(email, newPassword);
        emailService.sendPasswordResetConfirmation(email);
    }

    /**
     * Validates a token and returns associated user's email if valid
     * 
     * @param token Token to validate
     * @return Optional containing the user's email if token is valid, empty
     *         otherwise
     */
    public Optional<String> validateToken(String token) {
        return passwordResetTokenService.validateToken(token);
    }

}
