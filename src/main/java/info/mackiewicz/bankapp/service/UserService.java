package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.exception.UserNotFoundException;
import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final UsernameGeneratorService usernameGeneratorService;

    @Override
    @Transactional
    public User createUser(User user) {
        user = passwordService.ensurePasswordEncoded(user);
        user = usernameGeneratorService.generateUsername(user);
        User savedUser = userRepository.save(user);
        log.info("Created user with ID: {}", user.getId());
        return savedUser;
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        // Verify user exists
        getUserById(user.getId());

        user = passwordService.ensurePasswordEncoded(user);
        User saved = userRepository.save(user);
        log.info("Updated user with ID: {}", user.getId());

        return saved;
    }

    public void changeUsersPassword(String email, String newPassword) {
        userRepository.updatePasswordByEmail(email, passwordService.encodePassword(newPassword));
        log.info("Changed password for user with email: {}", email);
    }

    @Override
    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Override
    @Transactional
    public void deleteUser(Integer id) {
        User user = getUserById(id);
        userRepository.delete(user);
        log.info("Deleted user with ID: {}", id);
    }
    
    @Override
    public boolean checkUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
