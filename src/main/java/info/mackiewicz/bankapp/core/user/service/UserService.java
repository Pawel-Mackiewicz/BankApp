package info.mackiewicz.bankapp.core.user.service;

import info.mackiewicz.bankapp.core.user.exception.*;
import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.core.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.core.user.service.crud.UserCreationService;
import info.mackiewicz.bankapp.core.user.service.crud.UserOperationsService;
import info.mackiewicz.bankapp.core.user.service.crud.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Facade service that coordinates all user-related operations by delegating to specialized services.
 * This service provides a high-level interface for user management operations including:
 * - User creation and updates
 * - User querying and retrieval
 * - User deletion
 * - Password management
 *
 * @see UserCreationService
 * @see UserQueryService
 * @see UserOperationsService
 */
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserCreationService userCreationService;
    private final UserQueryService userQueryService;
    private final UserOperationsService userOperationsService;

    /**
     * Creates a new user in the system.
     *
     *
     * @param user The user object containing the information for the new user
     * @return The created user with generated ID
     * @throws UserFieldNullException if any of the required fields in the User object is null
     * @throws UserValidationException if the name validation fails for firstname or lastname
     * @throws InvalidAgeException if the user's age is less than 18 or greater than 120 years old
     * @throws DuplicatedEmailException if the email adress is already in use
     * @throws DuplicatedUsernameException if the username is already in use
     * @throws DuplicatedPeselException if the PESEL number is already in use
     * @throws DuplicatedPhoneNumberException if the phone number is already in use
     */
    public User createUser(User user) {
        return userCreationService.createUser(user);
    }

    /**
     * Updates an existing user's information.
     *
     * @param user The user object containing updated information
     * @return The updated user entity
     * @throws UserNotFoundException if the user does not exist
     */
    public User updateUser(User user) {
        return userOperationsService.updateUser(user);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id The unique identifier of the user
     * @return The user with the specified ID
     * @throws UserNotFoundException if no user is found with the given ID
     */
    public User getUserById(Integer id) {
        return userQueryService.getUserById(id);
    }

    /**
     * Retrieves a user by their ID with a pessimistic lock.
     * This method should be used when updating user data in concurrent scenarios.
     *
     * @param id The unique identifier of the user
     * @return The user with the specified ID
     * @throws UserNotFoundException if no user is found with the given ID
     */
    public User getUserByIdWithPessimisticLock(Integer id) {
        return userQueryService.getUserByIdWithPessimisticLock(id);
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username The unique username of the user
     * @return The user with the specified username
     * @throws UserNotFoundException if no user is found with the given username
     */
    public User getUserByUsername(String username) {
        return userQueryService.getUserByUsername(username);
    }

    /**
     * Retrieves all users from the system.
     *
     * @return A list of all users
     */
    public List<User> getAllUsers() {
        return userQueryService.getAllUsers();
    }

    /**
     * Deletes a user from the system.
     *
     * @param id The ID of the user to delete
     * @throws UserNotFoundException if no user exists with the given ID
     */
    public void deleteUser(Integer id) {
        userOperationsService.deleteUser(id);
    }

    /**
     * Checks if a username already exists in the system.
     *
     * @param username The username to check
     * @return true if the username exists, false otherwise
     */
    public boolean userExistsByUsername(String username) {
        return userQueryService.userExistsByUsername(username);
    }

    /**
     * Checks if a user exists with the given email.
     *
     * @param email The email to check
     * @return true if a user exists with the email, false otherwise
     * @throws InvalidEmailFormatException if the email format is invalid
     */
    public boolean userExistsByEmail(String email) {
        return userQueryService.userExistsByEmail(email);
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email The email address of the user
     * @return The user with the specified email
     * @throws UserNotFoundException if no user is found with the given email
     * @throws InvalidEmailFormatException if the email format is invalid
     */
    public User getUserByEmail(String email) {
        return userQueryService.getUserByEmail(email);
    }

    /**
     * Changes the password for a user identified by their email.
     *
     * @param email The email of the user whose password should be changed
     * @param newPassword The new password (in plain text)
     * @throws IllegalArgumentException if the email format is invalid
     */
    public void changeUsersPassword(String email, String newPassword) {
        userOperationsService.changeUsersPassword(email, newPassword);
    }

        /**
     * Changes the password for a user identified by their email.
     *
     * @param email The email of the user whose password should be changed
     * @param newPassword The new password (in plain text)
     * @throws IllegalArgumentException if the email format is invalid
     */
    public void changeUsersPassword(EmailAddress email, String newPassword) {
        userOperationsService.changeUsersPassword(email, newPassword);
    }
}
