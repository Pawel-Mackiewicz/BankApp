package info.mackiewicz.bankapp.security.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import info.mackiewicz.bankapp.security.exception.ExpiredPasswordResetTokenException;
import info.mackiewicz.bankapp.security.exception.InvalidPasswordResetTokenException;
import info.mackiewicz.bankapp.security.exception.TokenNotFoundException;
import info.mackiewicz.bankapp.security.exception.TooManyPasswordResetAttemptsException;
import info.mackiewicz.bankapp.security.exception.UsedPasswordResetTokenException;
import info.mackiewicz.bankapp.security.model.PasswordResetToken;
import info.mackiewicz.bankapp.security.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for managing password reset tokens
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetTokenService {

    private static final int MAX_ACTIVE_TOKENS_PER_USER = 2;

    private final PasswordResetTokenRepository tokenRepository;
    private final TokenHashingService tokenHashingService;

    /**
     * Creates a new password reset token for the given user
     * 
     * @param userEmail User email
     * @return Plain token to be sent to user
     */
    @Transactional
    public String createToken(String userEmail, String fullName) {
        // Check if user hasn't exceeded token limit
        long activeTokens = tokenRepository.countValidTokensByUserEmail(userEmail, LocalDateTime.now());
        if (activeTokens >= MAX_ACTIVE_TOKENS_PER_USER) {
            throw new TooManyPasswordResetAttemptsException();
        }

        // Generate new token and its hash
        String plainToken = tokenHashingService.generateToken();
        String tokenHash = tokenHashingService.hashToken(plainToken);

        // Create and save token entity with hash
        PasswordResetToken resetToken = new PasswordResetToken(tokenHash, userEmail, fullName);
        tokenRepository.save(resetToken);

        return plainToken; // Return plain token to be sent via email
    }

    /**
     * Validates a token and returns PasswordResetToken object if valid
     * 
     * @param token Token to validate
     * @return PasswordResetToken object if token is valid, empty otherwise
     * @throws TokenNotFoundException             if token is not found
     * @throws ExpiredPasswordResetTokenException if token is expired
     * @throws UsedPasswordResetTokenException    if token has already been used
     */
    public PasswordResetToken getValidatedToken(String token) {
        String tokenHash = tokenHashingService.hashToken(token);

        PasswordResetToken foundToken = tokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new TokenNotFoundException("Token not found"));

        validateToken(foundToken);

        return foundToken;
    }

    private void validateToken(PasswordResetToken foundToken) {
        if (foundToken.isExpired()) {
            throw new ExpiredPasswordResetTokenException("Token has expired");
        }
        if (foundToken.isUsed()) {
            throw new UsedPasswordResetTokenException("Token has already been used");
        }
    }

    /**
     * Method is deprecated and will be removed in future versions
     * Use validateAndGetToken instead
     * Validates a token and returns PasswordResetToken object if valid
     * 
     * @param token Token to validate
     * @return PasswordResetToken object if token is valid, empty otherwise
     * @see #getValidatedToken(String)
     */
    @Deprecated(forRemoval = true)
    public Optional<PasswordResetToken> validateToken(String token) {
        String tokenHash = tokenHashingService.hashToken(token);

        Optional<PasswordResetToken> foundToken = tokenRepository.findByTokenHash(tokenHash)
                .filter(PasswordResetToken::isValid);

        if (log.isDebugEnabled()) {
            log.debug("Validating token: found={}", foundToken.isPresent());

            if (foundToken.isPresent()) {
                PasswordResetToken resetToken = foundToken.get();
                log.debug("Token status: expired={}, used={}, valid={}",
                        resetToken.isExpired(), resetToken.isUsed(), resetToken.isValid());
            }
        }

        return foundToken;
    }

    /**
     * Marks a token as used and saves it to the database
     * 
     * @param token Token to consume
     * @return true if token was successfully consumed, false otherwise
     * @throws InvalidPasswordResetTokenException if token is not valid (expired or
     *                                            already used)
     */
    @Transactional
    public void consumeToken(PasswordResetToken token) {
        token.markAsUsed();
        tokenRepository.save(token);
    }

    /**
     * Marks a token as used and saves it to the database
     * 
     * @param token Token to consume
     * @return true if token was successfully consumed, false otherwise
     * @throws TokenNotFoundException             if token is not found
     * @throws InvalidPasswordResetTokenException if token is not valid (expired or
     *                                            already used)
     */
    @Deprecated
    @Transactional
    public void consumeToken(String token) {
        String tokenHash = tokenHashingService.hashToken(token);

        PasswordResetToken foundToken = tokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new TokenNotFoundException("Token not found"));

        consumeToken(foundToken);
    }

    public boolean isTokenPresent(String token) {
        String tokenHash = tokenHashingService.hashToken(token);
        return tokenRepository.findByTokenHash(tokenHash).isPresent();
    }

    /**
     * Validates if a user can request a new token
     * 
     * @param userEmail User to check
     * @return true if user can request new token, false otherwise
     */
    public boolean canRequestToken(String userEmail) {
        return tokenRepository.countValidTokensByUserEmail(userEmail, LocalDateTime.now()) < MAX_ACTIVE_TOKENS_PER_USER;
    }

    /**
     * Removes tokens older than 30 days from the database
     * 
     * @return Number of tokens deleted
     */
    @Transactional
    public int cleanupOldTokens() {
        return cleanupOldTokens(30);
    }

    /**
     * Removes tokens older than specified number of days
     * 
     * @param days Number of days after which tokens are considered old
     * @return Number of tokens deleted
     */
    @Transactional
    public int cleanupOldTokens(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return tokenRepository.deleteTokensOlderThan(cutoffDate);
    }
}