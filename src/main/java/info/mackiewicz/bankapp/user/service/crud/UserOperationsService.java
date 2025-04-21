package info.mackiewicz.bankapp.user.service.crud;

import info.mackiewicz.bankapp.system.security.password.PasswordService;
import info.mackiewicz.bankapp.user.exception.UserNotFoundException;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.user.repository.UserRepository;
import info.mackiewicz.bankapp.user.service.util.UserValidationService;
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
    public User updateUser(User user) {
        log.info("Starting user update process for ID: {}", user.getId());
        log.debug("Validating user existence");
        userValidationService.validateUserExists(user.getId());

        log.debug("Ensuring password is encoded for user: {}", user.getUsername());
        user = passwordService.ensurePasswordEncoded(user);

        log.debug("Saving updated user data");
        User saved = userRepository.save(user);
        log.info("Successfully updated user. ID: {}, username: {}", saved.getId(), saved.getUsername());
        return saved;
    }

    /**
     * Changes a user's password by their email address.
     * The new password will be encoded before saving.
     *
     * @param email       The email address of the user
     * @param newPassword The new password (in plain text)
     * @throws IllegalArgumentException if the email format is invalid
     */
    @Transactional
    public void changeUsersPassword(EmailAddress email, String newPassword) {
        log.info("Starting password change process for user with email: {}", email);
        log.debug("Encoding new password");
        String encodedPassword = passwordService.ensurePasswordEncoded(newPassword);

        log.debug("Updating password in database");
        userRepository.updatePasswordByEmail(email, encodedPassword);
        log.info("Successfully changed password for user with email: {}", email);
    }

    /**
     * Changes a user's password by their email address.
     * The new password will be encoded before saving.
     *
     * @param email       The email address of the user
     * @param newPassword The new password (in plain text)
     * @throws IllegalArgumentException if the email format is invalid
     */

    @Transactional
    public void changeUsersPassword(String email, String newPassword) {
        changeUsersPassword(new EmailAddress(email), newPassword);
    }

    /**
     * Deletes a user from the system.
     * Validates user existence before deletion.
     *
     * @param id The ID of the user to delete
     * @throws UserNotFoundException if the user does not exist
     */
    @Transactional
    public void deleteUser(Integer id) {
        log.info("Starting user deletion process for ID: {}", id);

        log.debug("Validating user existence");
        User user = userQueryService.getUserById(id);
        log.debug("Found user to delete: {}", user.getUsername());

        log.debug("Performing user deletion");
        userRepository.delete(user);
        log.info("Successfully deleted user. ID: {}, username: {}", id, user.getUsername());
    }
}