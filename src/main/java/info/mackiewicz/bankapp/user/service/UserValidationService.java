package info.mackiewicz.bankapp.user.service;

import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.user.exception.UserNotFoundException;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.vo.Email;
import info.mackiewicz.bankapp.user.model.vo.Pesel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for validating user data before operations like creation or update.
 * Ensures uniqueness of user identifiers (username, email, PESEL) and validates user existence.
 * All validation methods throw appropriate exceptions when validation fails.
 *
 * @see User
 * @see UserQueryService
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class UserValidationService {

    private final UserQueryService userQueryService;

    /**
     * Validates that the given username is unique in the system.
     *
     * @param username The username to validate
     * @throws IllegalArgumentException if the username already exists
     */
    public void validateUsernameUnique(String username) {
        if (userQueryService.userExistsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
    }

    /**
     * Validates that the given email address is unique in the system.
     *
     * @param email The email to validate as Email value object
     * @throws IllegalArgumentException if the email already exists
     * @see Email
     */
    public void validateEmailUnique(Email email) {
        if (userQueryService.userExistsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
    }

    /**
     * Validates that the given PESEL number is unique in the system.
     *
     * @param pesel The PESEL to validate as Pesel value object
     * @throws IllegalArgumentException if the PESEL already exists
     * @see Pesel
     */
    public void validatePeselUnique(Pesel pesel) {
        if (userQueryService.userExistsByPesel(pesel)) {
            throw new IllegalArgumentException("PESEL already exists: " + pesel);
        }
    }

    /**
     * Validates that a user with the given ID exists in the system.
     * This method will throw an exception if the user is not found, otherwise it returns normally.
     *
     * @param id The user ID to validate
     * @throws UserNotFoundException if no user exists with the given ID
     */
    public void validateUserExists(Integer id) {
        userQueryService.getUserById(id);
    }

    /**
     * Performs all necessary validations for a new user creation.
     * Validates uniqueness of username, email and PESEL if they are provided.
     *
     * @param user The user object to validate
     * @throws IllegalArgumentException if any of the unique fields (username, email, PESEL) already exist
     * @see User
     */
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