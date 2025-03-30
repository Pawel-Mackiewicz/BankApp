package info.mackiewicz.bankapp.security.service;

import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.security.exception.UsernameAlreadyTakenException;
import info.mackiewicz.bankapp.security.exception.UsernameSameException;
import info.mackiewicz.bankapp.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsernameValidationService {

    private final UserService userService;

    /**
     * Validates if the provided username is already taken.
     * 
     * @param newUsername The username to check
     * @throws UsernameAlreadyTakenException if the username is already taken
     * @throws UsernameSameException if the new username is the same as the old one
     */
    public void validateUsername(String newUsername, String oldUsername) {
        validateUsernameDifference(newUsername, oldUsername);
        validateUsernameAvailability(newUsername);
    }

    private void validateUsernameDifference(String newUsername, String oldUsername) {
        if (newUsername.equals(oldUsername)) {
            throw new UsernameSameException("New username is the same as the old one: " + newUsername);
        }
    }

    private void validateUsernameAvailability(String username) {
        if (userService.userExistsByUsername(username)) {
            throw new UsernameAlreadyTakenException("Username is already taken: " + username);
        }
    }


}
