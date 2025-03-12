package info.mackiewicz.bankapp.user.service;

import info.mackiewicz.bankapp.security.service.PasswordService;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for performing operations on existing users such as updates,
 * password changes, and deletions. This service ensures proper validation and
 * transaction management for all operations.
 *
 * @see User
 * @see UserValidationService
 * @see PasswordService
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class UserOperationsService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final UserQueryService userQueryService;
    private final UserValidationService userValidationService;

    /**
     * Updates an existing user's information.
     * Validates user existence and ensures password is properly encoded.
     *
     * @param user The user object containing updated information
     * @return The updated user entity
     * @throws UserNotFoundException if the user does not exist
     * @see User
     */
    @Transactional
    User updateUser(User user) {
        userValidationService.validateUserExists(user.getId());
        user = passwordService.ensurePasswordEncoded(user);
        User saved = userRepository.save(user);
        log.info("Updated user with ID: {}", user.getId());
        return saved;
    }

    /**
     * Changes a user's password by their email address.
     * The new password will be encoded before saving.
     *
     * @param email The email address of the user
     * @param newPassword The new password (in plain text)
     * @throws IllegalArgumentException if the email format is invalid
     */
    @Transactional
    void changeUsersPassword(String email, String newPassword) {
        userRepository.updatePasswordByEmail(email, passwordService.encodePassword(newPassword));
        log.info("Changed password for user with email: {}", email);
    }

    /**
     * Deletes a user from the system.
     * Validates user existence before deletion.
     *
     * @param id The ID of the user to delete
     * @throws UserNotFoundException if the user does not exist
     */
    @Transactional
    void deleteUser(Integer id) {
        // Ensure user exists before deletion, throws exception if not found
        User user = userQueryService.getUserById(id);
        
        userRepository.delete(user);
        log.info("Deleted user with ID: {}", id);
    }
}