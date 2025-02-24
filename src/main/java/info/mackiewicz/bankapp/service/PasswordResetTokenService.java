package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.model.PasswordResetToken;
import info.mackiewicz.bankapp.repository.PasswordResetTokenRepository;
import info.mackiewicz.bankapp.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service for managing password reset tokens
 */
@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {

    private static final int MAX_ACTIVE_TOKENS_PER_USER = 2;
    
    private final PasswordResetTokenRepository tokenRepository;

    /**
     * Creates a new password reset token for the given user
     * @param userEmail User email
     * @return Token value
     */
    @Transactional
    public String createToken(String userEmail) {
        // Check if user hasn't exceeded token limit
        long activeTokens = tokenRepository.countValidTokensByUserEmail(userEmail, LocalDateTime.now());
        if (activeTokens >= MAX_ACTIVE_TOKENS_PER_USER) {
            throw new IllegalStateException("Too many active reset tokens");
        }

        // Generate new token
        String token = JwtUtil.generateToken(userEmail);
        
        // Create and save token entity
        PasswordResetToken resetToken = new PasswordResetToken(token, userEmail);
        tokenRepository.save(resetToken);
        
        return token;
    }

    /**
     * Validates a token and returns associated user if valid
     * @param token Token to validate
     * @return Optional containing the user's email if token is valid, empty otherwise
     */
    public Optional<String> validateToken(String token) {
        return tokenRepository.findByToken(token)
                .filter(PasswordResetToken::isValid)
                .map(PasswordResetToken::getUserEmail);
    }

    /**
     * Marks a token as used if it's valid
     * @param token Token to consume
     * @return true if token was successfully consumed, false otherwise
     */
    @Transactional
    public boolean consumeToken(String token) {
        return tokenRepository.findByToken(token)
                .filter(PasswordResetToken::isValid)
                .map(resetToken -> {
                    resetToken.markAsUsed();
                    tokenRepository.save(resetToken);
                    return true;
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
}