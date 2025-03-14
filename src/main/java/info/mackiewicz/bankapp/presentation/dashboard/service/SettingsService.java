package info.mackiewicz.bankapp.presentation.dashboard.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import info.mackiewicz.bankapp.presentation.dashboard.dto.ChangePasswordRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.ChangeUsernameRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.UserSettingsDTO;
import info.mackiewicz.bankapp.presentation.exception.InvalidUserException;
import info.mackiewicz.bankapp.security.service.PasswordService;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SettingsService {

    private final UserService userService;
    private final PasswordService passwordService;

    public UserSettingsDTO getUserSettings(Integer userId) {
        User requestedUser = userService.getUserById(userId);
        if (!requestedUser.getId().equals(userId)) {
            throw new InvalidUserException("Access denied");
        }
        UserSettingsDTO dto = UserSettingsDTO.fromUser(requestedUser);
        return dto;
    }

    @Transactional
    public boolean changePassword(User user, ChangePasswordRequest request) {

        if (!passwordService.verifyPassword(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidUserException("Incorrect current password");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidUserException("New password and confirmation do not match");
        }

        user.setPassword(request.getNewPassword());
        userService.updateUser(user);
        log.info("Changed password for user: {}", user.getUsername());
        
        return true;
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