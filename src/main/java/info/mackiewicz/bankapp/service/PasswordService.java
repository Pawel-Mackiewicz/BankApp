package info.mackiewicz.bankapp.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PasswordService {

    private final PasswordEncoder passwordEncoder;

    /**
     * Ensures the user's password is encoded using the configured PasswordEncoder.
     * If the password is already encoded, returns the user unchanged.
     *
     * @param user the user with password to check/encode
     * @return user with encoded password
     */
    public User ensurePasswordEncoded(User user) {
        String password = user.getPassword();
        if (!isPasswordEncoded(password)) {
            user.setPassword(passwordEncoder.encode(password));
            log.info("Password encoded for user with ID: {}", user.getId());
        }
        return user;
    }

    /**
     * Checks if the password is already encoded.
     *
     * @param password the password to check
     * @return true if the password is already encoded, false otherwise
     */
    private boolean isPasswordEncoded(String password) {
        return password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$");
    }



    /**
     * Verifies the raw password against the encoded password.
     *
     * @param rawPassword     the raw password to verify
     * @param encodedPassword the encoded password to verify against
     * @return true if the passwords match, false otherwise
     */
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

}
