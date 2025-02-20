package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.exception.DuplicatedUserException;
import info.mackiewicz.bankapp.exception.UserNotFoundException;
import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public UserService(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    @Override
    public User createUser(User user) {
        if (checkUsernameExists(user.getUsername())) {
            throw new DuplicatedUserException("Username " + user.getUsername() + " is already taken");
        }
        User savedUser = userRepository.save(user);
        log.info("Created user: {}", user.getUsername());
        return savedUser;
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
    public User updateUser(User user) {
        // Verify user exists
        getUserById(user.getId());
        User saved = userRepository.save(user);
        log.info("Updated user: {}", user.getUsername());
        return saved;
    }

    @Override
    public boolean checkUsernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        User user = getUserById(id);
        userRepository.delete(user);
        log.info("Deleted user with id: {}", id);
    }

    @Override
    public String encodePassword(String password) {
        return passwordService.encodePassword(password);
    }

    @Override
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordService.verifyPassword(rawPassword, encodedPassword);
    }
}
