package info.mackiewicz.bankapp.presentation.dashboard.settings.service;

import info.mackiewicz.bankapp.presentation.dashboard.settings.exception.InvalidPasswordException;
import info.mackiewicz.bankapp.presentation.dashboard.settings.exception.PasswordSameException;
import info.mackiewicz.bankapp.presentation.dashboard.settings.exception.PasswordsMismatchException;
import info.mackiewicz.bankapp.shared.service.PasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordValidationService {

    private final PasswordService passwordService;
    
    /**
     * Performs all password validations for password change.
     * 
     * @param currentPassword Current password (plain)
     * @param newPassword New password (plain)
     * @param confirmPassword Confirmation of new password
     * @param encodedCurrentPassword Current password (encoded)
     * @throws InvalidPasswordException When current password is incorrect
     * @throws PasswordsMismatchException When new password and confirmation don't match
     * @throws PasswordSameException When new password is the same as the old one
     */
    public void validatePasswordChange(String currentPassword, String newPassword, 
                               String confirmPassword, String encodedCurrentPassword) {
        validateCurrentPassword(currentPassword, encodedCurrentPassword);
        validatePasswordMatch(newPassword, confirmPassword);
        validatePasswordDifferentiation(newPassword, encodedCurrentPassword);
        
        log.info("All password validations passed successfully");
    }
    
    /**
     * Validates if the provided current password is correct for the given encoded password.
     * 
     * @param currentPassword Raw current password provided by user
     * @param encodedPassword Encoded password stored in the system
     * @throws InvalidPasswordException When current password is incorrect
     */
    public void validateCurrentPassword(String currentPassword, String encodedPassword) {
        if (!passwordService.verifyPassword(currentPassword, encodedPassword)) {
            throw new InvalidPasswordException("Incorrect current password");
        }
        
        log.debug("Current password validation passed");
    }
    
    /**
     * Validates if password and confirmation match.
     * 
     * @param password Password to check
     * @param confirmPassword Password confirmation
     * @throws PasswordsMismatchException When passwords don't match
     */
    public void validatePasswordMatch(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new PasswordsMismatchException("New password and confirmation do not match");
        }
        
        log.debug("Password match validation passed");
    }
    
    /**
     * Checks if the new password is different from the old one.
     * 
     * @param newPassword New plain password
     * @param encodedOldPassword Encoded old password
     * @throws PasswordSameException When new password is the same as old one
     */
    public void validatePasswordDifferentiation(String newPassword, String encodedOldPassword) {
        if (passwordService.verifyPassword(newPassword, encodedOldPassword)) {
            throw new PasswordSameException("New password is the same as the old one");
        }
        
        log.debug("Password differentiation validation passed");
    }
    
}
