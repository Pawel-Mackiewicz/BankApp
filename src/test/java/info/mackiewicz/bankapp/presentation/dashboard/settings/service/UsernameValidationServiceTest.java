package info.mackiewicz.bankapp.presentation.dashboard.settings.service;

import info.mackiewicz.bankapp.core.user.service.UserService;
import info.mackiewicz.bankapp.presentation.dashboard.settings.exception.ForbiddenUsernameException;
import info.mackiewicz.bankapp.presentation.dashboard.settings.exception.UsernameAlreadyTakenException;
import info.mackiewicz.bankapp.presentation.dashboard.settings.exception.UsernameSameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsernameValidationServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UsernameValidationService usernameValidationService;

    @Nested
    @DisplayName("validateUsername method tests")
    class ValidateUsernameTests {
        
        @Test
        @DisplayName("Should validate successfully when username is valid")
        void shouldValidateSuccessfully_whenUsernameIsValid() {
            // Arrange
            String newUsername = "validUser123";
            String oldUsername = "oldUser456";
            when(userService.userExistsByUsername(anyString())).thenReturn(false);

            // Act & Assert
            usernameValidationService.validateUsername(newUsername, oldUsername);

            // Verify
            verify(userService).userExistsByUsername(newUsername);
        }

        @Test
        @DisplayName("Should throw UsernameSameException when new username equals old username")
        void shouldThrowUsernameSameException_whenNewUsernameEqualsOldUsername() {
            // Arrange
            String username = "sameUser";

            // Act & Assert
            assertThatThrownBy(() -> 
                usernameValidationService.validateUsername(username, username))
                .isInstanceOf(UsernameSameException.class)
                .hasMessageContaining("New username is the same as the old one");
        }

        @Test
        @DisplayName("Should throw UsernameAlreadyTakenException when username exists")
        void shouldThrowUsernameAlreadyTakenException_whenUsernameExists() {
            // Arrange
            String newUsername = "takenUser";
            String oldUsername = "oldUser";
            when(userService.userExistsByUsername(newUsername)).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> 
                usernameValidationService.validateUsername(newUsername, oldUsername))
                .isInstanceOf(UsernameAlreadyTakenException.class)
                .hasMessageContaining("Username is already taken");
        }

        @Test
        @DisplayName("Should throw ForbiddenUsernameException when username is forbidden")
        void shouldThrowForbiddenUsernameException_whenUsernameIsForbidden() {
            // Arrange
            String newUsername = "admin";
            String oldUsername = "oldUser";
            when(userService.userExistsByUsername(anyString())).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> 
                usernameValidationService.validateUsername(newUsername, oldUsername))
                .isInstanceOf(ForbiddenUsernameException.class)
                .hasMessageContaining("Username is forbidden");
        }

        @Test
        @DisplayName("Should throw ForbiddenUsernameException for forbidden username regardless of case")
        void shouldThrowForbiddenUsernameException_forForbiddenUsernameRegardlessOfCase() {
            // Arrange
            String newUsername = "AdMiN";
            String oldUsername = "oldUser";
            when(userService.userExistsByUsername(anyString())).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> 
                usernameValidationService.validateUsername(newUsername, oldUsername))
                .isInstanceOf(ForbiddenUsernameException.class)
                .hasMessageContaining("Username is forbidden");
        }
    }
}