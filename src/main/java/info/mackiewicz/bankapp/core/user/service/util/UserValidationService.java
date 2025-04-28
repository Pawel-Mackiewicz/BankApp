package info.mackiewicz.bankapp.core.user.service.util;

import info.mackiewicz.bankapp.core.user.exception.*;
import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.core.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.core.user.model.vo.Pesel;
import info.mackiewicz.bankapp.core.user.model.vo.PhoneNumber;
import info.mackiewicz.bankapp.core.user.service.crud.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

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

    // Only allow letters (English and Polish)
    private static final String LETTERS_REGEX = "^[A-Za-zĄĆĘŁŃÓŚŹŻąćęłńóśźż]+$";
    // Age constraints
    private static final int MINIMUM_AGE = 18;
    private static final int MAXIMUM_AGE = 120;
    private final UserQueryService userQueryService;

    /**
     * Validates the provided User object to ensure all required fields are valid
     * and meet the application's registration criteria. This method performs the
     * following checks:
     * - Ensures no required field is null.
     * - Validates the format and content of first and last names.
     * - Ensures the username is unique across the system.
     * - Ensures the email address is unique across the system.
     * - Ensures the PESEL number is unique across the system.
     * - Ensures the phone number is unique across the system.
     * - Verifies that the user's age meets the application's age constraints.
     *
     * @param user The User object to validate. It must contain all necessary data
     *             for registration, including first name, last name, username,
     *             email, PESEL, phone number, and date of birth.
     * @throws UserFieldNullException if any of the required fields in the User object is null
     * @throws UserValidationException if the name validation fails for firstname or lastname
     * @throws InvalidAgeException if the user's age is less than 18 or greater than 120 years old
     * @throws DuplicatedEmailException if the email adress is already in use
     * @throws DuplicatedUsernameException if the username is already in use
     * @throws DuplicatedPeselException if the PESEL number is already in use
     * @throws DuplicatedPhoneNumberException if the phone number is already in use
     */
    public void validateNewUser(User user) {
        log.info("Starting validation for new user registration");

        checkNulls(user);

        validateName(user.getFirstname());
        validateName(user.getLastname());
        validateUsernameUnique(user.getUsername());
        validateEmailUnique(user.getEmail());
        validatePeselUnique(user.getPesel());
        validatePhoneNumberUnique(user.getPhoneNumber());
        validateAge(user.getDateOfBirth());

        log.info("Successfully completed validation for new user registration");
    }

    private void checkNulls(User user) {
        if (
                user.getFirstname() == null ||
                        user.getLastname() == null ||
                        user.getEmail() == null ||
                        user.getPesel() == null ||
                        user.getPhoneNumber() == null ||
                        user.getDateOfBirth() == null
        ) {
            log.warn("Null values detected in user object: {}", user);
            throw new UserFieldNullException("Null values detected in user object");
        }
    }

    private void validateName(String name) {
        if (!isValidLetters(name)) {
            log.warn("Invalid name: {}", name);
            throw new UserValidationException("Invalid name: " + name);
        }
    }

    private boolean isValidLetters(String input) {
        return input != null && input.matches(LETTERS_REGEX);
    }

    private void validatePhoneNumberUnique(PhoneNumber phoneNumber) {
        log.debug("Validating phone number uniqueness: {}", phoneNumber);
        if (userQueryService.userExistsByPhoneNumber(phoneNumber)) {
            log.warn("Attempt to use existing phone number: {}", phoneNumber);
            throw new DuplicatedPhoneNumberException("Phone number already in use: " + phoneNumber);
        }
        log.debug("Phone number {} is unique", phoneNumber);
    }

    /**
     * Validates that the given username is unique in the system.
     *
     * @param username The username to validate
     * @throws DuplicatedUsernameException if the username already exists
     */
    public void validateUsernameUnique(String username) {
        log.debug("Validating username uniqueness: {}", username);
        if (userQueryService.userExistsByUsername(username)) {
            log.warn("Attempt to use existing username: {}", username);
            throw new DuplicatedUsernameException("Username already in use: " + username);
        }
        log.debug("Username {} is unique", username);
    }

    /**
     * Validates that the given email address is unique in the system.
     *
     * @param email The email to validate as Email value object
     * @throws DuplicatedEmailException if the email already exists
     * @see EmailAddress
     */
    public void validateEmailUnique(EmailAddress email) {
        log.debug("Validating email uniqueness: {}", email);
        if (userQueryService.userExistsByEmail(email)) {
            log.warn("Attempt to use existing email: {}", email);
            throw new DuplicatedEmailException("Email already in use: " + email.toString());
        }
        log.debug("Email {} is unique", email);
    }

    /**
     * Validates that the given PESEL number is unique in the system.
     *
     * @param pesel The PESEL to validate as Pesel value object
     * @throws DuplicatedPeselException if the PESEL already exists
     * @see Pesel
     */
    public void validatePeselUnique(Pesel pesel) {
        log.debug("Validating PESEL uniqueness: {}", pesel);
        if (userQueryService.userExistsByPesel(pesel)) {
            log.warn("Attempt to use existing PESEL: {}", pesel);
            throw new DuplicatedPeselException("PESEL already in use: " + pesel);
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

        if (userQueryService.userExistsById(id)) {
            log.debug("User with ID {} exists", id);
        } else {
            log.warn("Validation failed - user with ID {} does not exist", id);
            throw new UserNotFoundException("User with ID " + id + " does not exist");
        }
    }

    /**
     * Validates that the user's age is at least 18 years old and not older than 120 years.
     * The age is calculated based on the provided birth date.
     *
     * @param birthDate The user's birth date
     * @throws InvalidAgeException if the user's age is less than 18 or greater than 120
     */
    public void validateAge(LocalDate birthDate) {
        log.debug("Validating user age based on birth date: {}", birthDate);

        if (birthDate == null) {
            log.warn("Birth date is null, cannot validate age");
            throw new InvalidAgeException("Birth date is required");
        }

        LocalDate currentDate = LocalDate.now();
        int age = Period.between(birthDate, currentDate).getYears();

        log.debug("Calculated user age: {} years", age);

        if (age < MINIMUM_AGE) {
            log.warn("User age validation failed: user is under minimum age of {}", MINIMUM_AGE);
            throw new InvalidAgeException("User must be at least " + MINIMUM_AGE + " years old");
        }

        if (age > MAXIMUM_AGE) {
            log.warn("User age validation failed: user exceeds maximum age of {}", MAXIMUM_AGE);
            throw new InvalidAgeException("User cannot be older than " + MAXIMUM_AGE + " years old");
        }

        log.debug("User age validation successful: {} years", age);
    }
}