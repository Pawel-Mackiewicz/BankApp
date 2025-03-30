package info.mackiewicz.bankapp.presentation.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import info.mackiewicz.bankapp.presentation.dashboard.dto.ChangePasswordRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.ChangeUsernameRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.UserSettingsDTO;
import info.mackiewicz.bankapp.presentation.dashboard.service.SettingsService;
import info.mackiewicz.bankapp.security.service.PasswordValidationService;
import info.mackiewicz.bankapp.security.service.UsernameValidationService;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.vo.Email;
import info.mackiewicz.bankapp.user.model.vo.PhoneNumber;
import info.mackiewicz.bankapp.user.service.UserService;

@DisplayName("SettingsService Tests")
class SettingsServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordValidationService passwordValidationService;

    @Mock
    private UsernameValidationService usernameValidationService;

    @InjectMocks
    private SettingsService settingsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = createTestUser();
    }

    private User createTestUser() {
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");
        user.setFirstname("Test");
        user.setLastname("User");
        user.setEmail(new Email("test@example.com"));
        user.setPhoneNumber(new PhoneNumber("123456789"));
        user.setPassword("currentPassword");
        return user;
    }

    @Nested
    @DisplayName("getUserSettings tests")
    class GetUserSettingsTests {

        @Test
        @DisplayName("Should correctly map user to UserSettingsDTO")
        void testGetUserSettings() {
            // Act
            UserSettingsDTO settings = settingsService.getUserSettings(testUser);

            // Assert
            assertNotNull(settings, "Settings should not be null");
            assertEquals("testuser", settings.getUsername(), "Username should match");
            assertEquals("test@example.com", settings.getEmail(), "Email should match");
            assertEquals("+48123456789", settings.getPhoneNumber(), "Phone number should match");
            assertEquals("Test", settings.getFirstname(), "First name should match");
            assertEquals("User", settings.getLastname(), "Last name should match");
        }
    }

    @Nested
    @DisplayName("changePassword tests")
    class ChangePasswordTests {

        @Test
        @DisplayName("Should successfully change password when all validations pass")
        void testChangePasswordSuccess() {
            // Arrange
            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setCurrentPassword("currentPassword");
            request.setPassword("newPassword");
            request.setConfirmPassword("newPassword");

            // No exception means validation passed
            doNothing().when(passwordValidationService).validatePasswordChange(
                eq("currentPassword"),
                eq("newPassword"),
                eq("newPassword"),
                eq("currentPassword")
            );

            // Act
            settingsService.changePassword(testUser, request);

            // Assert
            verify(userService, times(1)).updateUser(testUser);
            assertEquals("newPassword", testUser.getPassword(), "Password should be updated");
        }

        @Test
        @DisplayName("Should delegate password validation to PasswordValidationService")
        void testPasswordValidationDelegation() {
            // Arrange
            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setCurrentPassword("currentPassword");
            request.setPassword("newPassword");
            request.setConfirmPassword("newPassword");

            // Act
            settingsService.changePassword(testUser, request);

            // Assert
            verify(passwordValidationService, times(1)).validatePasswordChange(
                eq("currentPassword"),
                eq("newPassword"),
                eq("newPassword"),
                any()
            );
        }
    }

    @Nested
    @DisplayName("changeUsername tests")
    class ChangeUsernameTests {

        @Test
        @DisplayName("Should successfully change username when validation passes")
        void testChangeUsernameSuccess() {
            // Arrange
            String newUsername = "newUsername";
            ChangeUsernameRequest request = new ChangeUsernameRequest();
            request.setNewUsername(newUsername);
            
            doNothing().when(usernameValidationService).validateUsername(
                eq(newUsername),
                eq("testuser")
            );

            // Act
            settingsService.changeUsername(testUser, request);

            // Assert
            verify(userService, times(1)).updateUser(testUser);
            assertEquals(newUsername, testUser.getUsername(), "Username should be updated");
        }

        @Test
        @DisplayName("Should delegate username validation to UsernameValidationService")
        void testUsernameValidationDelegation() {
            // Arrange
            String newUsername = "newUsername";
            ChangeUsernameRequest request = new ChangeUsernameRequest();
            request.setNewUsername(newUsername);

            // Act
            settingsService.changeUsername(testUser, request);

            // Assert
            verify(usernameValidationService, times(1)).validateUsername(
                eq(newUsername),
                eq("testuser")
            );
        }
    }
}