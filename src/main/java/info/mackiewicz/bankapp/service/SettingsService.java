package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.dto.ChangePasswordRequest;
import info.mackiewicz.bankapp.dto.ChangeUsernameRequest;
import info.mackiewicz.bankapp.exception.InvalidUserException;
import info.mackiewicz.bankapp.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class SettingsService implements SettingsServiceInterface {

    private final UserService userService;

    public SettingsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public User getUserSettings(Integer userId) {
        return userService.getUserById(userId);
    }

    @Override
    @Transactional
    public boolean changePassword(User user, ChangePasswordRequest request) {
        // Sprawdzanie poprawności obecnego hasła
        if (!userService.verifyPassword(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidUserException("Incorrect current password");
        }

        // Sprawdzanie zgodności nowego hasła z potwierdzeniem
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidUserException("New password and confirmation do not match");
        }

        // Ustawienie i zapisanie nowego hasła
        user.setPassword(userService.encodePassword(request.getNewPassword()));
        userService.updateUser(user);
        log.info("Changed password for user: {}", user.getUsername());
        
        return true;
    }

    @Override
    @Transactional
    public void changeUsername(User user, ChangeUsernameRequest request) {
        String oldUsername = user.getUsername();
        String newUsername = request.getNewUsername();
        
        // Sprawdzanie czy nowa nazwa jest inna niż obecna
        if (newUsername.equals(user.getUsername())) {
            throw new InvalidUserException("New username is the same as the current one");
        }

        // Sprawdzanie czy nowa nazwa nie jest już zajęta
        if (userService.checkUsernameExists(newUsername)) {
            throw new InvalidUserException("Username " + newUsername + " is already taken");
        }

        user.setUsername(newUsername);
        userService.updateUser(user);
        log.info("Changed username for user {}: {} -> {}", user.getId(), oldUsername, newUsername);
    }
}