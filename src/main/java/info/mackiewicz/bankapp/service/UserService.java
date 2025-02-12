package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.exception.DuplicatedUserException;
import info.mackiewicz.bankapp.exception.InvalidUserException;
import info.mackiewicz.bankapp.exception.UserNotFoundException;
import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        if (userRepository.existsByPESEL(user.getPESEL())) {
            throw new DuplicatedUserException();
        }

        user.setRoles(setDefaultRole());
        user.setUsername(generateUsername(user));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    private Set<String> setDefaultRole() {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        return roles;
    }

    private String generateUsername(User user) {
        String baseUsername = user.getFirstname().toLowerCase() + "." + user.getLastname().toLowerCase();
        return baseUsername + generateUniqueID(user.getEmail());
    }

    private String generateUniqueID(String email) {
        int hash = email.hashCode();

        String sHash = Integer.toString(hash);
        if (sHash.length() > 6) {
            return sHash.substring(0, 6);
        } else
            return sHash;
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new InvalidUserException("Cannot update user without ID");
        }
        // Check if user exists
        userRepository.findById(user.getId())
                .orElseThrow(() -> new UserNotFoundException("User with ID " + user.getId() + " not found"));

        // If password is provided, encode it
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(user);
    }

    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User with ID " + id + " not found");
        }
        userRepository.deleteById(id);
    }
}
