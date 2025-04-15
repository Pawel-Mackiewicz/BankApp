package info.mackiewicz.bankapp.user.service.crud;

import info.mackiewicz.bankapp.security.service.PasswordService;
import info.mackiewicz.bankapp.user.exception.*;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.repository.UserRepository;
import info.mackiewicz.bankapp.user.service.util.UserValidationService;
import info.mackiewicz.bankapp.user.service.util.UsernameGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for user creation operations. This service handles the complete
 * process of creating new users in the system, including:<br>
 * - Validation of user data uniqueness<br>
 * - Automatic username generation when not provided<br>
 * - Password encoding<br>
 * - Persisting the user in the database<br><br>
 * <p>
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
     * Creates a new user in the system. This method performs the following steps:<br>
     * 1. Generates a username if one is not provided<br>
     * 2. Ensures the password is properly encoded<br>
     * 3. Saves the user to the database<br>
     * <p>
     * The entire operation is performed within a transaction to ensure data consistency.
     *
     * @param user The user object containing the information for the new user
     * @return The created user with generated ID and all fields processed
     * @throws UserFieldNullException if any of the required fields in the User object is null
     * @throws UserValidationException if the name validation fails for firstname or lastname
     * @throws InvalidAgeException if the user's age is less than 18 or greater than 120 years old
     * @throws DuplicatedEmailException if the email adress is already in use
     * @throws DuplicatedUsernameException if the username is already in use
     * @throws DuplicatedPeselException if the PESEL number is already in use
     * @throws DuplicatedPhoneNumberException if the phone number is already in use
     */
    @Transactional
    public User createUser(User user) {

        User userWithUsername = generateUsername(user);
        log.debug("Generated username: {}", user.getUsername());

        User validatedUser = validateNewUser(userWithUsername);
        log.debug("Validated new user data");

        User userWithPassword = passwordService.ensurePasswordEncoded(validatedUser);
        log.debug("Password encoded");

        User savedUser = userRepository.save(userWithPassword);
        log.info("Successfully created user. ID: {}, username: {}", savedUser.getId(), savedUser.getUsername());

        return savedUser;
    }

    private User generateUsername(User user) {

        // Skip username generation if user already has a username defined
        if (user.getUsername() != null) return user;

        String username = usernameGeneratorService.generateUsername(user.getFirstname(), user.getLastname(), user.getEmail().toString());
        user.setUsername(username);
        return user;
    }

    private User validateNewUser(User user) {
        userValidationService.validateNewUser(user);
        return user;
    }
}