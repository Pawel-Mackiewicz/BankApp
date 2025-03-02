package info.mackiewicz.bankapp.security.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import info.mackiewicz.bankapp.security.model.PasswordResetToken;
import info.mackiewicz.bankapp.security.repository.PasswordResetTokenRepository;
import info.mackiewicz.bankapp.shared.exception.TooManyPasswordResetAttemptsException;
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
        
        return plainToken;  // Return plain token to be sent via email
    }

    /**
     * Validates a token and returns associated user's email if valid
     * @param token Token to validate
     * @return Optional containing the user's email if token is valid, empty otherwise
     */
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
     * Marks a token as used if it's valid
     * @param token Token to consume
     * @return true if token was successfully consumed, false otherwise
     */
    @Transactional
    public boolean consumeToken(String token) {
        String tokenHash = tokenHashingService.hashToken(token);
        
        Optional<PasswordResetToken> foundToken = tokenRepository.findByTokenHash(tokenHash);
        
        if (log.isDebugEnabled()) {
            log.debug("Consuming token: found={}", foundToken.isPresent());
            
            if (foundToken.isPresent()) {
                PasswordResetToken resetToken = foundToken.get();
                log.debug("Token status: expired={}, used={}, valid={}",
                    resetToken.isExpired(), resetToken.isUsed(), resetToken.isValid());
            }
        }
        
        return tokenRepository.findByTokenHash(tokenHash)
                .filter(resetToken -> {
                    boolean isValid = resetToken.isValid();
                    if (log.isDebugEnabled()) {
                        log.debug("Token valid for consumption: {}", isValid);
                    }
                    return isValid;
                })
                .map(resetToken -> {
                    try {
                        resetToken.markAsUsed();
                        tokenRepository.save(resetToken);
                        log.debug("Token successfully consumed");
                        return true;
                    } catch (Exception e) {
                        log.error("Error consuming token", e);
                        return false;
                    }
                })
                .orElse(false);
    }

    /**
     * Validates if a user can request a new token
     * @param userEmail User to check
     * @return true if user can request new token, false otherwise
     */
    public boolean canRequestToken(String userEmail) {
        return tokenRepository.countValidTokensByUserEmail(userEmail, LocalDateTime.now()) < MAX_ACTIVE_TOKENS_PER_USER;
    }

    /**
     * Removes tokens older than 30 days from the database
     * @return Number of tokens deleted
     */
    @Transactional
    public int cleanupOldTokens() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        return tokenRepository.deleteTokensOlderThan(cutoffDate);
    }
    
    /**
     * Removes tokens older than specified number of days
     * @param days Number of days after which tokens are considered old
     * @return Number of tokens deleted
     */
    @Transactional
    public int cleanupOldTokens(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return tokenRepository.deleteTokensOlderThan(cutoffDate);
    }
}