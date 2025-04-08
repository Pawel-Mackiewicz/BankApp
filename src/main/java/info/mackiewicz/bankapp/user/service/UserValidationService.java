package info.mackiewicz.bankapp.user.service;

import java.time.LocalDate;
import java.time.Period;

import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.user.exception.DuplicatedUserException;
import info.mackiewicz.bankapp.user.exception.InvalidAgeException;
import info.mackiewicz.bankapp.user.exception.UserNotFoundException;
import info.mackiewicz.bankapp.user.exception.UserValidationException;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.vo.EmailAddress;
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

    // Only allow letters (English and Polish)
    private static final String LETTERS_REGEX = "^[A-Za-zĄĆĘŁŃÓŚŹŻąćęłńóśźż]+$";
    
    // Age constraints
    private static final int MINIMUM_AGE = 18;
    private static final int MAXIMUM_AGE = 120;

    /**
     * Performs all necessary validations for a new user creation.
     * Validates the user's first name, last name, username, email, PESEL and phone number.
     * Validates that the first name and last name contain only letters (English and Polish) if they are provided.
     * Validates uniqueness of username, email and PESEL if they are provided.
     * Validates that the user is at least 18 years old and not older than 120 years.
     *
     * @param user The user object to validate
     * @throws UserValidationException if any of the unique fields (username,
     *                                  email, PESEL) already exist
     * @throws InvalidAgeException if the user's age is less than 18 or greater than 120
     * @see User
     */
    public void validateNewUser(User user) {
        log.info("Starting validation for new user registration");
        log.debug("Validating user data: username={}, email={}",
                user.getUsername(),
                user.getEmail() != null ? user.getEmail().toString() : "null");

        if(user.getFirstname() != null) {
          validateName(user.getFirstname());
        }

        if(user.getLastname() != null) {
            validateName(user.getLastname());
        }
        
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
        
        if (user.getDateOfBirth() != null) {
            validateAge(user.getDateOfBirth());
        }

        log.info("Successfully completed validation for new user registration");
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
            throw new DuplicatedUserException("Phone number already in use: " + phoneNumber);
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
            throw new DuplicatedUserException("Username already in use: " + username);
        }
        log.debug("Username {} is unique", username);
    }

    /**
     * Validates that the given email address is unique in the system.
     *
     * @param email The email to validate as Email value object
     * @throws UserValidationException if the email already exists
     * @see EmailAddress
     */
    public void validateEmailUnique(EmailAddress email) {
        log.debug("Validating email uniqueness: {}", email);
        if (userQueryService.userExistsByEmail(email)) {
            log.warn("Attempt to use existing email: {}", email);
            throw new DuplicatedUserException("Email already in use");
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
            throw new DuplicatedUserException("PESEL already in use: " + pesel);
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