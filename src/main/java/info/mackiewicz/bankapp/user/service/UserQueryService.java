package info.mackiewicz.bankapp.user.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import info.mackiewicz.bankapp.user.exception.InvalidEmailFormatException;
import info.mackiewicz.bankapp.user.exception.InvalidPeselFormatException;
import info.mackiewicz.bankapp.user.exception.UserNotFoundException;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.vo.Email;
import info.mackiewicz.bankapp.user.model.vo.Pesel;
import info.mackiewicz.bankapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for querying user data from the database. Provides methods for retrieving
 * user information based on various criteria such as ID, username, email, and PESEL.
 * This service ensures thread-safe access to user data and implements proper exception handling
 * for cases when users are not found.
 *
 * @see info.mackiewicz.bankapp.user.model.User
 * @see info.mackiewicz.bankapp.user.repository.UserRepository
 * @see info.mackiewicz.bankapp.shared.exception.UserNotFoundException
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
    User getUserById(Integer id) {
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
    User getUserByIdWithPessimisticLock(Integer id) {
        return userRepository.findByIdWithPessimisticLock(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username The unique username of the user
     * @return The user with the specified username
     * @throws UserNotFoundException if no user is found with the given username
     * @see User
     */
    User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
        .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email The email address as a string
     * @return The user with the specified email
     * @throws UserNotFoundException if no user is found with the given email
     * @throws IllegalArgumentException if the email format is invalid
     * @see Email
     */
    User getUserByEmail(String email) {
        return getUserByEmail(new Email(email));
    }

    /**
     * Retrieves a user by their email address using Email value object.
     *
     * @param email The email address as an Email value object
     * @return The user with the specified email
     * @throws UserNotFoundException if no user is found with the given email
     * @see Email
     */
    User getUserByEmail(Email email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    /**
     * Retrieves all users from the database.
     *
     * @return A list of all users
     * @see User
     */
    List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Checks if a username already exists in the system.
     *
     * @param username The username to check
     * @return true if the username exists, false otherwise
     */
    boolean userExistsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Checks if a user exists with the given email.
     *
     * @param email The email to check as a string
     * @return true if a user exists with the email, false otherwise
     * @throws InvalidEmailFormatException if the email format is invalid
     * @see Email
     */
    boolean userExistsByEmail(String email) {
        return userExistsByEmail(new Email(email));
    }

    /**
     * Checks if a user exists with the given email using Email value object.
     *
     * @param email The email to check as an Email value object
     * @return true if a user exists with the email, false otherwise
     * @see Email
     */
    public boolean userExistsByEmail(Email email) {
        return userRepository.existsByEmail(email);
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
        return userRepository.existsByPesel(pesel);
    }
}