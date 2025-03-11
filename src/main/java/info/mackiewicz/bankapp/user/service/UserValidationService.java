package info.mackiewicz.bankapp.user.service;

import info.mackiewicz.bankapp.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service responsible for user data validation
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class UserValidationService {

    private final UserQueryService userQueryService;

    public void validateUsernameUnique(String username) {
        if (userQueryService.checkUsernameExists(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
    }

    public void validateEmailUnique(String email) {
        if (userQueryService.userExistsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
    }

    public void validatePeselUnique(String pesel) {
        if (userQueryService.userExistsByPesel(pesel)) {
            throw new IllegalArgumentException("PESEL already exists: " + pesel);
        }
    }

    public void validateUserExists(Integer id) {
        userQueryService.getUserById(id);
    }

    public void validateNewUser(User user) {
        if (user.getUsername() != null) {
            validateUsernameUnique(user.getUsername());
        }
        if (user.getEmail() != null) {
            validateEmailUnique(user.getEmail());
        }
        if (user.getPesel() != null) {
            validatePeselUnique(user.getPesel());
        }
    }
}