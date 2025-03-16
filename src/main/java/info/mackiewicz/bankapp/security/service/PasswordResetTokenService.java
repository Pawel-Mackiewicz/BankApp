package info.mackiewicz.bankapp.security.service;

import java.time.LocalDateTime;

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
    private static final int DEFAULT_CLEANUP_DAYS = 30;

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
        log.info("Starting password reset token creation process for user: {}", userEmail);
        log.debug("Validating token creation eligibility for user: {}", userEmail);
        
        validateTokenCreation(userEmail);

        log.debug("Generating new token and hash for user: {}", userEmail);
        String plainToken = tokenHashingService.generateToken();
        String tokenHash = tokenHashingService.hashToken(plainToken);

        saveNewToken(userEmail, fullName, tokenHash);
        log.info("Successfully created password reset token for user: {}", userEmail);

        return plainToken;
    }

    private void validateTokenCreation(String userEmail) {
        // Check if user hasn't exceeded token limit
        if (!canRequestToken(userEmail)) {
            log.warn("User {} has exceeded the limit of active tokens", userEmail);
            throw new TooManyPasswordResetAttemptsException();
        }
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
     * @throws ExpiredPasswordResetTokenException if token is expired
     * @throws UsedPasswordResetTokenException    if token has already been used
     */
    public PasswordResetToken getValidatedToken(String token) {
        log.debug("Starting token validation process");
        String tokenHash = tokenHashingService.hashToken(token);
        
        log.debug("Looking up token by hash");
        PasswordResetToken foundToken = findTokenByHash(tokenHash);
        
        log.debug("Validating token state for user: {}", foundToken.getUserEmail());
        validateToken(foundToken);
        
        log.info("Successfully validated token for user: {}", foundToken.getUserEmail());
        return foundToken;
    }

    private PasswordResetToken findTokenByHash(String tokenHash) {
        try {
            PasswordResetToken foundToken = tokenRepository.findByTokenHash(tokenHash)
                    .orElseThrow(() -> new TokenNotFoundException("Token not found"));
            log.debug("Found token in database");
            return foundToken;
        } catch (TokenNotFoundException e) {
            log.warn("Token lookup failed: token hash not found in database");
            throw e;
        }
    }

    private void validateToken(PasswordResetToken foundToken) {
        String userEmail = foundToken.getUserEmail();
        if (foundToken.isExpired()) {
            log.warn("Token validation failed: expired token for user: {}", userEmail);
            throw new ExpiredPasswordResetTokenException("Token has expired");
        }
        if (foundToken.isUsed()) {
            log.warn("Token validation failed: already used token for user: {}", userEmail);
            throw new UsedPasswordResetTokenException("Token has already been used");
        }
        log.debug("Token validation successful for user: {}", userEmail);
    }

    /**
     * Marks a token as used and saves it to the database
     * 
     * @param token Token to consume
     * @throws InvalidPasswordResetTokenException if token is not valid (expired or
     *                                            already used)
     */
    @Transactional
    public void consumeToken(PasswordResetToken token) {
        log.info("Consuming password reset token for user: {}", token.getUserEmail());
        token.markAsUsed();
        tokenRepository.save(token);
        log.debug("Token marked as used and saved for user: {}", token.getUserEmail());
    }

    public boolean isTokenPresent(String token) {
        log.debug("Checking if token exists in database");
        String tokenHash = tokenHashingService.hashToken(token);
        boolean exists = tokenRepository.findByTokenHash(tokenHash).isPresent();
        log.debug("Token existence check result: {}", exists);
        return exists;
    }

    /**
     * Validates if a user can request a new token
     *
     * @param userEmail User to check
     * @return true if user can request new token, false otherwise
     */
    public boolean canRequestToken(String userEmail) {
        log.debug("Checking if user can request new token: {}", userEmail);
        long activeTokens = tokenRepository.countValidTokensByUserEmail(userEmail, LocalDateTime.now());
        boolean canRequest = activeTokens < MAX_ACTIVE_TOKENS_PER_USER;
        
        if (!canRequest) {
            log.warn("User {} has reached maximum number of active tokens: {}", userEmail, activeTokens);
        } else {
            log.debug("User {} has {} active tokens, can request new token", userEmail, activeTokens);
        }
        
        return canRequest;
    }

    /**
     * Removes tokens older than 30 days from the database
     *
     * @return Number of tokens deleted
     */
    @Transactional
    public int cleanupOldTokens() {
        log.info("Starting default token cleanup ({}  days old)", DEFAULT_CLEANUP_DAYS);
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
        log.info("Starting token cleanup for tokens older than {} days", days);
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        int deletedCount = tokenRepository.deleteTokensOlderThan(cutoffDate);
        log.info("Cleanup completed: removed {} expired tokens", deletedCount);
        return deletedCount;
    }
}