package info.mackiewicz.bankapp.presentation.dashboard.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import info.mackiewicz.bankapp.presentation.dashboard.dto.ChangePasswordRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.ChangeUsernameRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.UserSettingsDTO;
import info.mackiewicz.bankapp.presentation.exception.InvalidUserException;
import info.mackiewicz.bankapp.security.service.PasswordValidationService;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.interfaces.PersonalInfo;
import info.mackiewicz.bankapp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SettingsService {

    private final UserService userService;
    private final PasswordValidationService passwordValidationService;

    /**
     * Retrieves the user settings for the authenticated user.
     *
     * @param user The authenticated user
     * @return UserSettingsDTO containing user settings information
     */
    public UserSettingsDTO getUserSettings(PersonalInfo user) {
        return UserSettingsDTO.fromUser(user);
    }

    /**
     * Updates the user settings for the authenticated user.
     *
     * @param user        The authenticated user
     * @param settingsDTO The new settings to be applied
     * @throws InvalidPasswordException   if the current password is incorrect
     * @throws PasswordsMismatchException if the new password and confirmation do
     *                                    not match
     * @throws PasswordSameException      if the new password is the same as the old
     *                                    one
     */
    @Transactional
    public void changePassword(User user, ChangePasswordRequest request) {
        // Delegate all password validation to the PasswordValidationService
        passwordValidationService.validatePasswordChange(
                request.getCurrentPassword(),
                request.getPassword(),
                request.getConfirmPassword(),
                user.getPassword());

        // Set and encode the new password
        user.setPassword(request.getPassword());
        userService.updateUser(user);
        log.info("Changed password for user: {}", user.getUsername());
    }

    @Transactional
    public void changeUsername(User user, ChangeUsernameRequest request) {
        String oldUsername = user.getUsername();
        String newUsername = request.getNewUsername();

        if (newUsername.equals(user.getUsername())) {
            throw new InvalidUserException("New username is the same as the current one");
        }

        if (userService.userExistsByUsername(newUsername)) {
            throw new InvalidUserException("Username " + newUsername + " is already taken");
        }

        user.setUsername(newUsername);
        userService.updateUser(user);
        log.info("Changed username for user {}: {} -> {}", user.getId(), oldUsername, newUsername);
    }
}