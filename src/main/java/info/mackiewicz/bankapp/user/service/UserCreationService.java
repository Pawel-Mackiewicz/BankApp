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
 * Service responsible for user creation operations
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class UserCreationService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final UsernameGeneratorService usernameGeneratorService;

    @Transactional
    public User createUser(User user) {
        log.debug("Creating user: {}", user);
        // Generate username only if not already set
        if (!StringUtils.hasText(user.getUsername())) {
            user = usernameGeneratorService.generateUsername(user);
        }
        
        // Ensure password is encoded
        user = passwordService.ensurePasswordEncoded(user);
        
        // Save user
        User savedUser = userRepository.save(user);
        log.debug("Created user with ID: {}", savedUser.getId());
        
        return savedUser;
    }
}