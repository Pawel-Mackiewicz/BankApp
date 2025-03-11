package info.mackiewicz.bankapp.user.service;

import info.mackiewicz.bankapp.user.exception.UserNotFoundException;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.vo.Email;
import info.mackiewicz.bankapp.user.model.vo.Pesel;
import info.mackiewicz.bankapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

/**
 * Service responsible for querying user data
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class UserQueryService {

    private final UserRepository userRepository;

    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    @Transactional
    public User getUserByIdWithPessimisticLock(Integer id) {
        return userRepository.findByIdWithPessimisticLock(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
        .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }
    public User getUserByEmail(String email) {
        return getUserByEmail(new Email(email));
    }

    public User getUserByEmail(Email email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean checkUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean userExistsByEmail(Email email) {
        return userRepository.existsByEmail(email);
    }
    public boolean userExistsByEmail(String email) {
        return userExistsByEmail(new Email(email));
    }

    public boolean userExistsByPesel(String pesel) {
        return userExistsByPesel(new Pesel(pesel));
    }

    public boolean userExistsByPesel(Pesel pesel) {
        return userRepository.existsByPesel(pesel);
    }
}