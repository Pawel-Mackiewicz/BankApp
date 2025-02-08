package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.exception.DuplicatedUserException;
import info.mackiewicz.bankapp.exception.InvalidUserException;
import info.mackiewicz.bankapp.exception.UserNotFoundException;
import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection of UserRepository
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        if (userRepository.existsByPESEL(user.getPESEL())) {
            throw new DuplicatedUserException();
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new InvalidUserException();
        }
        return userRepository.save(user);
    }

    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found for deletion");
        }
        userRepository.deleteById(id);
    }
}
