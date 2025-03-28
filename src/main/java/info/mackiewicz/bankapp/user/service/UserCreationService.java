package info.mackiewicz.bankapp.user.service;

import info.mackiewicz.bankapp.security.service.PasswordService;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Service responsible for user creation operations. This service handles the complete
 * process of creating new users in the system, including:
 * - Validation of user data uniqueness
 * - Automatic username generation when not provided
 * - Password encoding
 * - Persisting the user in the database
 *
 * The service ensures that all necessary steps are performed atomically within
 * a transaction to maintain data consistency.
 *
 * @see UserValidationService
 * @see UsernameGeneratorService
 * @see PasswordService
 * @see User
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class UserCreationService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final UsernameGeneratorService usernameGeneratorService;
    private final UserValidationService userValidationService;

    /**
     * Creates a new user in the system. This method performs the following steps:
     * 1. Validates that the user's unique fields (username, email, PESEL) don't already exist
     * 2. Generates a username if one is not provided
     * 3. Ensures the password is properly encoded
     * 4. Saves the user to the database
     *
     * The entire operation is performed within a transaction to ensure data consistency.
     *
     * @param user The user object containing the information for the new user
     * @return The created user with generated ID and all fields processed
     * @throws IllegalArgumentException if any unique fields (username, email, PESEL) already exist
     * @throws IllegalStateException if required fields are missing or invalid
     */
    @Transactional
    User createUser(User user) {
        log.info("Starting user creation process for email: {}", user.getEmail());
        userValidationService.validateNewUser(user);

        // Generate username only if not already set
        if (!StringUtils.hasText(user.getUsername())) {
            log.debug("Generating username for user with email: {}", user.getEmail());
            user = usernameGeneratorService.generateUsername(user);
            log.debug("Generated username: {}", user.getUsername());
        }
        
        // Ensure password is encoded
        log.debug("Encoding password for user: {}", user.getUsername());
        user = passwordService.ensurePasswordEncoded(user);
        
        // Save user
        User savedUser = userRepository.save(user);
        log.info("Successfully created user. ID: {}, username: {}", savedUser.getId(), savedUser.getUsername());
        
        return savedUser;
    }
}