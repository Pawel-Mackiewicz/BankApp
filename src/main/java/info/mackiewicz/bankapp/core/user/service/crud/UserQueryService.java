package info.mackiewicz.bankapp.core.user.service.crud;

import info.mackiewicz.bankapp.core.user.exception.InvalidEmailFormatException;
import info.mackiewicz.bankapp.core.user.exception.InvalidPeselFormatException;
import info.mackiewicz.bankapp.core.user.exception.UserNotFoundException;
import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.core.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.core.user.model.vo.Pesel;
import info.mackiewicz.bankapp.core.user.model.vo.PhoneNumber;
import info.mackiewicz.bankapp.core.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service responsible for querying user data from the database. Provides methods for retrieving
 * user information based on various criteria such as ID, username, email, and PESEL.
 * This service ensures thread-safe access to user data and implements proper exception handling
 * for cases when users are not found.
 *
 * @see User
 * @see UserRepository
 * @see UserNotFoundException
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class UserQueryService {

    private final UserRepository userRepository;

    /**
     * Retrieves a user by their ID.
     *
     * @param id The unique identifier of the user
     * @return The user with the specified ID
     * @throws UserNotFoundException if no user is found with the given ID
     * @see User
     */
    public User getUserById(Integer id) {
        log.debug("Querying user by ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    /**
     * Retrieves a user by their ID with a pessimistic lock for concurrent access control.
     * This method acquires a database-level lock on the user record to prevent concurrent modifications.
     *
     * @param id The unique identifier of the user
     * @return The user with the specified ID
     * @throws UserNotFoundException if no user is found with the given ID
     * @see User
     */
    @Transactional
    public User getUserByIdWithPessimisticLock(Integer id) {
        log.debug("Querying user by ID with pessimistic lock: {}", id);
        try {
            return userRepository.findByIdWithPessimisticLock(id)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        } catch (Exception e) {
            log.error("Error while acquiring pessimistic lock for user ID: {}", id, e);
            throw e;
        }
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username The unique username of the user
     * @return The user with the specified username
     * @throws UserNotFoundException if no user is found with the given username
     * @see User
     */
    public User getUserByUsername(String username) {
        log.debug("Querying user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email The email address as a string
     * @return The user with the specified email
     * @throws UserNotFoundException    if no user is found with the given email
     * @throws IllegalArgumentException if the email format is invalid
     * @see EmailAddress
     */
    public User getUserByEmail(String email) {
        log.debug("Querying user by email string: {}", email);
        return getUserByEmail(new EmailAddress(email));
    }

    /**
     * Retrieves a user by their email address using Email value object.
     *
     * @param email The email address as an Email value object
     * @return The user with the specified email
     * @throws UserNotFoundException if no user is found with the given email
     * @see EmailAddress
     */
    public User getUserByEmail(EmailAddress email) {
        log.debug("Querying user by email object: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    /**
     * Retrieves all users from the database.
     *
     * @return A list of all users
     * @see User
     */
    public List<User> getAllUsers() {
        log.debug("Querying all users from database");
        List<User> users = userRepository.findAll();
        log.debug("Found {} users in database", users.size());
        return users;
    }

    /**
     * Checks if a user exists with the given ID.
     *
     * @param id The unique identifier of the user
     * @return true if the user exists, false otherwise
     */
    public boolean userExistsById(Integer id) {
        log.debug("Checking if user exists by ID: {}", id);
        return userRepository.existsById(id);
    }

    /**
     * Checks if a username already exists in the system.
     *
     * @param username The username to check
     * @return true if the username exists, false otherwise
     */
    public boolean userExistsByUsername(String username) {
        log.debug("Checking if username exists: {}", username);
        return userRepository.existsByUsername(username);
    }

    /**
     * Checks if a user exists with the given email.
     *
     * @param email The email to check as a string
     * @return true if a user exists with the email, false otherwise
     * @throws InvalidEmailFormatException if the email format is invalid
     * @see EmailAddress
     */
    public boolean userExistsByEmail(String email) {
        log.debug("Checking if user exists by email string: {}", email);
        return userExistsByEmail(new EmailAddress(email));
    }

    /**
     * Checks if a user exists with the given email using Email value object.
     *
     * @param email The email to check as an Email value object
     * @return true if a user exists with the email, false otherwise
     * @see EmailAddress
     */
    public boolean userExistsByEmail(EmailAddress email) {
        log.debug("Checking if user exists by Email object: {}", email);
        boolean exists = userRepository.existsByEmail(email);
        log.debug("User with email {} {} in the system", email, exists ? "exists" : "does not exist");
        return exists;
    }

    /**
     * Checks if a user exists with the given PESEL number.
     *
     * @param pesel The PESEL number to check as a string
     * @return true if a user exists with the PESEL, false otherwise
     * @throws InvalidPeselFormatException if the PESEL format is invalid
     * @see Pesel
     */
    public boolean userExistsByPesel(String pesel) {
        log.debug("Checking if user exists by PESEL string: {}", pesel);
        return userExistsByPesel(new Pesel(pesel));
    }

    /**
     * Checks if a user exists with the given PESEL using Pesel value object.
     *
     * @param pesel The PESEL to check as a Pesel value object
     * @return true if a user exists with the PESEL, false otherwise
     * @see Pesel
     */
    public boolean userExistsByPesel(Pesel pesel) {
        log.debug("Checking if user exists by Pesel object: {}", pesel);
        boolean exists = userRepository.existsByPesel(pesel);
        log.debug("User with PESEL {} {} in the system", pesel, exists ? "exists" : "does not exist");
        return exists;
    }

    public boolean userExistsByPhoneNumber(PhoneNumber phoneNumber) {
        log.debug("Checking if user exists by phone number: {}", phoneNumber);
        boolean exists = userRepository.existsByPhoneNumber(phoneNumber);
        log.debug("User with phone number {} {} in the system", phoneNumber, exists ? "exists" : "does not exist");
        return exists;
    }
}