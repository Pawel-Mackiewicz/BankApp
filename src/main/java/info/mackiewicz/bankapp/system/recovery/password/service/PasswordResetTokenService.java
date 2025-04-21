package info.mackiewicz.bankapp.system.recovery.password.service;

import info.mackiewicz.bankapp.system.recovery.password.exception.*;
import info.mackiewicz.bankapp.system.token.model.PasswordResetToken;
import info.mackiewicz.bankapp.system.token.repository.PasswordResetTokenRepository;
import info.mackiewicz.bankapp.system.token.service.TokenOperationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service for managing password reset tokens
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetTokenService {

    private static final int MAX_ACTIVE_TOKENS_PER_USER = 2;
    private static final int DEFAULT_CLEANUP_DAYS = 30;

    private final PasswordResetTokenRepository tokenRepository;
    private final TokenOperationsService tokenOperationsService;

    /**
     * Creates a new password reset token for the given user
     * 
     * @param userEmail User email
     * @return Plain token to be sent to user
     * @throws TooManyPasswordResetAttemptsException if user has exceeded active
     *                                               token limit
     */
    @Transactional
    public String createToken(String userEmail, String fullName) {
        log.debug("Starting password reset token creation process for user: {}", userEmail);
        log.debug("Validating token creation eligibility for user: {}", userEmail);

        if (!canRequestNewToken(userEmail)) {
            throw new TooManyPasswordResetAttemptsException("User exceeded token limit: " + userEmail);
        }

        log.debug("Generating new token and hash for user: {}", userEmail);
        String plainToken = tokenOperationsService.generateToken();
        String tokenHash = tokenOperationsService.hashToken(plainToken);

        saveNewToken(userEmail, fullName, tokenHash);
        log.debug("Successfully created password reset token for user: {}", userEmail);

        return plainToken;
    }

    private void saveNewToken(String userEmail, String fullName, String tokenHash) {
        // Create and save token entity with hash
        PasswordResetToken resetToken = new PasswordResetToken(tokenHash, userEmail, fullName);
        tokenRepository.save(resetToken);
    }

    /**
     * Validates a token and returns PasswordResetToken object if valid
     * 
     * @param token Token to validate
     * @return PasswordResetToken object if token is valid, empty otherwise
     * @throws TokenNotFoundException             if token is not found
     * @throws ExpiredTokenException if token is expired
     * @throws UsedTokenException    if token has already been used
     */
    public PasswordResetToken getValidatedToken(String token) {
        log.debug("Starting token validation process");
        String tokenHash = tokenOperationsService.hashToken(token);

        PasswordResetToken foundToken = findTokenByHash(tokenHash);

        log.debug("Validating token state for user: {}", foundToken.getUserEmail());
        validateToken(foundToken);

        log.debug("Successfully validated token for user: {}", foundToken.getUserEmail());
        return foundToken;
    }

    /**
     * 
     * @param tokenHash
     * @return PasswordResetToken object if token is found
     * @throws TokenNotFoundException if token is not found
     */
    private PasswordResetToken findTokenByHash(String tokenHash) {
        PasswordResetToken foundToken = tokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new TokenNotFoundException("Token not found"));
        log.debug("Found token in database");
        return foundToken;
    }

    /**
     * Validates the state of the found token
     *
     * @param foundToken
     * @throws ExpiredTokenException if token is expired
     * @throws UsedTokenException    if token has already been used
     */
    private void validateToken(PasswordResetToken foundToken) {
        String userEmail = foundToken.getUserEmail();
        if (foundToken.isExpired()) {
            throw new ExpiredTokenException("Token has expired for user: " + userEmail);
        }
        if (foundToken.isUsed()) {
            throw new UsedTokenException("Token has already been used for user: " + userEmail);
        }
    }

    /**
     * Marks a token as used and saves it to the database
     * 
     * @param token Token to consume
     * @throws TokenException if token is not valid (expired or
     *                                            already used)
     */
    @Transactional
    public void consumeToken(PasswordResetToken token) {
        log.debug("Consuming password reset token for user: {}", token.getUserEmail());
        token.markAsUsed();
        tokenRepository.save(token);
        log.debug("Token marked as used and saved for user: {}", token.getUserEmail());
    }

    public boolean isTokenPresent(String token) {
        log.debug("Checking if token exists in database");
        String tokenHash = tokenOperationsService.hashToken(token);
        boolean exists = tokenRepository.findByTokenHash(tokenHash).isPresent();
        log.debug("Token existence check result: {}", exists);
        return exists;
    }

    /**
     * Validates if a user can request a new token
     *
     * @param userEmail User to check
     * @return true if user can request new token, false otherwise
     * @throws TooManyPasswordResetAttemptsException if user has exceeded token
     *                                               limit
     */
    public boolean canRequestNewToken(String userEmail) {
        log.debug("Checking if user can request new token: {}", userEmail);
        int activeTokens = tokenRepository.countValidTokensByUserEmail(userEmail, LocalDateTime.now());
        return activeTokens < MAX_ACTIVE_TOKENS_PER_USER;
    }

    /**
     * Removes tokens older than 30 days from the database
     *
     * @return Number of tokens deleted
     */
    @Transactional
    public int cleanupOldTokens() {
        log.debug("Starting default token cleanup ({}  days old)", DEFAULT_CLEANUP_DAYS);
        return cleanupOldTokens(DEFAULT_CLEANUP_DAYS);
    }

    /**
     * Removes tokens older than specified number of days
     *
     * @param days Number of days after which tokens are considered old
     * @return Number of tokens deleted
     */
    @Transactional
    public int cleanupOldTokens(int days) {
        log.debug("Starting token cleanup for tokens older than {} days", days);
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        int deletedCount = tokenRepository.deleteTokensOlderThan(cutoffDate);
        log.debug("Cleanup completed: removed {} expired tokens", deletedCount);
        return deletedCount;
    }
}