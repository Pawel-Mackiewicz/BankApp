package info.mackiewicz.bankapp.security.service;

import info.mackiewicz.bankapp.presentation.dashboard.exception.InvalidPasswordException;
import info.mackiewicz.bankapp.presentation.dashboard.exception.PasswordSameException;
import info.mackiewicz.bankapp.presentation.dashboard.exception.PasswordsMismatchException;
import info.mackiewicz.bankapp.presentation.dashboard.service.PasswordValidationService;
import info.mackiewicz.bankapp.system.security.password.PasswordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordValidationServiceTest {

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    private PasswordValidationService passwordValidationService;

    @Nested
    @DisplayName("validatePasswordChange tests")
    class ValidatePasswordChangeTest {

        @Test
        @DisplayName("Should pass when all validations are successful")
        void validatePasswordChange_WhenAllValidationsPass_ThenNoException() {
            // Arrange
            String currentPassword = "Current123";
            String newPassword = "New123456";
            String confirmPassword = "New123456";
            String encodedCurrentPassword = "encoded_current123";
            
            when(passwordService.verifyPassword(currentPassword, encodedCurrentPassword)).thenReturn(true);
            when(passwordService.verifyPassword(newPassword, encodedCurrentPassword)).thenReturn(false);

            // Act & Assert - no exception
            passwordValidationService.validatePasswordChange(currentPassword, newPassword, confirmPassword, encodedCurrentPassword);
        }
    }

    @Nested
    @DisplayName("validateCurrentPassword tests")
    class ValidateCurrentPasswordTest {

        @Test
        @DisplayName("Should pass when current password is correct")
        void validateCurrentPassword_WhenPasswordCorrect_ThenNoException() {
            // Arrange
            String currentPassword = "Current123";
            String encodedPassword = "encoded_current123";
            
            when(passwordService.verifyPassword(currentPassword, encodedPassword)).thenReturn(true);

            // Act & Assert - no exception
            passwordValidationService.validateCurrentPassword(currentPassword, encodedPassword);
            
            verify(passwordService).verifyPassword(currentPassword, encodedPassword);
        }

        @Test
        @DisplayName("Should fail when current password is incorrect")
        void validateCurrentPassword_WhenPasswordIncorrect_ThenThrowException() {
            // Arrange
            String currentPassword = "Wrong123";
            String encodedPassword = "encoded_current123";
            
            when(passwordService.verifyPassword(currentPassword, encodedPassword)).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> 
                passwordValidationService.validateCurrentPassword(currentPassword, encodedPassword))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessage("Incorrect current password");
        }
    }

    @Nested
    @DisplayName("validatePasswordMatch tests")
    class ValidatePasswordMatchTest {

        @Test
        @DisplayName("Should pass when passwords match")
        void validatePasswordMatch_WhenPasswordsMatch_ThenNoException() {
            // Arrange
            String password = "NewPass123";
            String confirmPassword = "NewPass123";

            // Act & Assert - no exception
            passwordValidationService.validatePasswordMatch(password, confirmPassword);
        }

        @Test
        @DisplayName("Should fail when passwords don't match")
        void validatePasswordMatch_WhenPasswordsDontMatch_ThenThrowException() {
            // Arrange
            String password = "NewPass123";
            String confirmPassword = "Different123";

            // Act & Assert
            assertThatThrownBy(() -> 
                passwordValidationService.validatePasswordMatch(password, confirmPassword))
                .isInstanceOf(PasswordsMismatchException.class)
                .hasMessage("New password and confirmation do not match");
        }
    }

    @Nested
    @DisplayName("validatePasswordDifferentiation tests")
    class ValidatePasswordDifferentiationTest {

        @Test
        @DisplayName("Should pass when new password is different")
        void validatePasswordDifferentiation_WhenPasswordsDifferent_ThenNoException() {
            // Arrange
            String newPassword = "NewPass123";
            String encodedOldPassword = "encoded_old_pass";
            
            when(passwordService.verifyPassword(newPassword, encodedOldPassword)).thenReturn(false);

            // Act & Assert - no exception
            passwordValidationService.validatePasswordDifferentiation(newPassword, encodedOldPassword);
            
            verify(passwordService).verifyPassword(newPassword, encodedOldPassword);
        }

        @Test
        @DisplayName("Should fail when new password is same as old")
        void validatePasswordDifferentiation_WhenPasswordsSame_ThenThrowException() {
            // Arrange
            String newPassword = "SamePass123";
            String encodedOldPassword = "encoded_same_pass";
            
            when(passwordService.verifyPassword(newPassword, encodedOldPassword)).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> 
                passwordValidationService.validatePasswordDifferentiation(newPassword, encodedOldPassword))
                .isInstanceOf(PasswordSameException.class)
                .hasMessage("New password is the same as the old one");
        }
    }
}