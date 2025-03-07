package info.mackiewicz.bankapp.security.service;

import java.security.SecureRandom;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for secure token generation and hashing using SHA-256
 */
@Service
@Slf4j
public class TokenHashingService {

    private static final int TOKEN_LENGTH = 32;
    private final SecureRandom secureRandom;

    public TokenHashingService() {
        this.secureRandom = new SecureRandom();
    }

    /**
     * Generates a secure random token
     * @return Base64URL encoded token string
     */
    public String generateToken() {
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    /**
     * Hashes a token using SHA-256 (deterministyczne hashowanie)
     * @param token Token to hash
     * @return Hashed token string
     */
    public String hashToken(String token) {
        if (token == null) {
            throw new IllegalArgumentException("Token cannot be null");
        }
        
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            String hashedToken = Base64.getEncoder().encodeToString(hashBytes);
            
            if (log.isTraceEnabled()) {
                log.trace("Hashed token for verification");
            }
            
            return hashedToken;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Verifies a token against a stored hash
     * @param token Token to verify
     * @param storedHash Stored hash to verify against
     * @return true if token matches hash
     */
    public boolean verifyToken(String token, String storedHash) {
        if (token == null || storedHash == null) {
            throw new IllegalArgumentException("Token and hash cannot be null");
        }
        
        String computedHash = hashToken(token);
        return computedHash.equals(storedHash);
    }
}