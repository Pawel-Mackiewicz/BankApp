package info.mackiewicz.bankapp.user.service;

import info.mackiewicz.bankapp.security.service.PasswordService;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for operations on existing users
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class UserOperationsService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final UserQueryService userQueryService;

    @Transactional
    public User updateUser(User user) {
        // Verify user exists
        userQueryService.getUserById(user.getId());

        user = passwordService.ensurePasswordEncoded(user);
        User saved = userRepository.save(user);
        log.info("Updated user with ID: {}", user.getId());

        return saved;
    }

    @Transactional
    public void changeUsersPassword(String email, String newPassword) {
        userRepository.updatePasswordByEmail(email, passwordService.encodePassword(newPassword));
        log.info("Changed password for user with email: {}", email);
    }

    @Transactional
    public void deleteUser(Integer id) {
        User user = userQueryService.getUserById(id);
        userRepository.delete(user);
        log.info("Deleted user with ID: {}", id);
    }
}