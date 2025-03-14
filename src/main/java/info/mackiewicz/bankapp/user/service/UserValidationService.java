package info.mackiewicz.bankapp.user.service;

import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.user.exception.UserNotFoundException;
import info.mackiewicz.bankapp.user.exception.UserValidationException;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.vo.Email;
import info.mackiewicz.bankapp.user.model.vo.Pesel;
import info.mackiewicz.bankapp.user.model.vo.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for validating user data before operations like creation
 * or update.
 * Ensures uniqueness of user identifiers (username, email, PESEL) and validates
 * user existence.
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
     * Performs all necessary validations for a new user creation.
     * Validates uniqueness of username, email and PESEL if they are provided.
     *
     * @param user The user object to validate
     * @throws UserValidationException if any of the unique fields (username,
     *                                  email, PESEL) already exist
     * @see User
     */
    public void validateNewUser(User user) {
        log.info("Starting validation for new user registration");
        log.debug("Validating user data: username={}, email={}",
                user.getUsername(),
                user.getEmail() != null ? user.getEmail().toString() : "null");

        if (user.getUsername() != null) {
            validateUsernameUnique(user.getUsername());
        }
        if (user.getEmail() != null) {
            validateEmailUnique(user.getEmail());
        }
        if (user.getPesel() != null) {
            validatePeselUnique(user.getPesel());
        }
        if (user.getPhoneNumber() != null) {
            validatePhoneNumberUnique(user.getPhoneNumber());
        }

        log.info("Successfully completed validation for new user registration");
    }

    private void validatePhoneNumberUnique(PhoneNumber phoneNumber) {
        log.debug("Validating phone number uniqueness: {}", phoneNumber);
        if (userQueryService.userExistsByPhoneNumber(phoneNumber)) {
            log.warn("Attempt to use existing phone number: {}", phoneNumber);
            throw new UserValidationException("Phone number already in use: " + phoneNumber);
        }
        log.debug("Phone number {} is unique", phoneNumber);
    }

    /**
     * Validates that the given username is unique in the system.
     *
     * @param username The username to validate
     * @throws UserValidationException if the username already exists
     */
    public void validateUsernameUnique(String username) {
        log.debug("Validating username uniqueness: {}", username);
        if (userQueryService.userExistsByUsername(username)) {
            log.warn("Attempt to use existing username: {}", username);
            throw new UserValidationException("Username already in use: " + username);
        }
        log.debug("Username {} is unique", username);
    }

    /**
     * Validates that the given email address is unique in the system.
     *
     * @param email The email to validate as Email value object
     * @throws UserValidationException if the email already exists
     * @see Email
     */
    public void validateEmailUnique(Email email) {
        log.debug("Validating email uniqueness: {}", email);
        if (userQueryService.userExistsByEmail(email)) {
            log.warn("Attempt to use existing email: {}", email);
            throw new UserValidationException("Email already in use");
        }
        log.debug("Email {} is unique", email);
    }

    /**
     * Validates that the given PESEL number is unique in the system.
     *
     * @param pesel The PESEL to validate as Pesel value object
     * @throws UserValidationException if the PESEL already exists
     * @see Pesel
     */
    public void validatePeselUnique(Pesel pesel) {
        log.debug("Validating PESEL uniqueness: {}", pesel);
        if (userQueryService.userExistsByPesel(pesel)) {
            log.warn("Attempt to use existing PESEL: {}", pesel);
            throw new UserValidationException("PESEL already in use: " + pesel);
        }
        log.debug("PESEL {} is unique", pesel);
    }

    /**
     * Validates that a user with the given ID exists in the system.
     * This method will throw an exception if the user is not found, otherwise it
     * returns normally.
     *
     * @param id The user ID to validate
     * @throws UserNotFoundException if no user exists with the given ID
     */
    public void validateUserExists(Integer id) {
        log.debug("Validating user existence for ID: {}", id);
        try {
            userQueryService.getUserById(id);
            log.debug("User with ID {} exists", id);
        } catch (UserNotFoundException e) {
            log.warn("Validation failed - user with ID {} does not exist", id);
            throw e;
        }
    }
}