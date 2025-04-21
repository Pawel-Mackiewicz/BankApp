package info.mackiewicz.bankapp.system.token.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Service for secure token generation, validation and hashing using SHA-256
 */
@Service
@Slf4j
public class TokenOperationsService {

    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int TOKEN_LENGTH = 32;
    private final SecureRandom secureRandom;

    public TokenOperationsService() {
        this.secureRandom = new SecureRandom();
    }

    /**
     * Generates a secure random token
     * @return Base64URL encoded token string
     */
    public String generateToken() {
        log.debug("Generating new secure token with length: {}", TOKEN_LENGTH);
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);

        log.debug("Successfully generated new secure token");

        return token;
    }

    /**
     * Hashes a token using SHA-256 (deterministic hash)
     * @param token Token to hash
     * @return Hashed token string
     */
    public String hashToken(String token) {
        if (token == null) {
            log.error("Attempt to hash null token");
            throw new IllegalArgumentException("Token cannot be null");
        }
        
        try {
            log.debug("Hashing token using {} algorithm", HASH_ALGORITHM);
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            String hashedToken = Base64.getEncoder().encodeToString(hashBytes);
            
            log.debug("Successfully hashed token");
            return hashedToken;
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to hash token: {} algorithm not available", HASH_ALGORITHM, e);
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
        log.debug("Starting token verification process");
        
        if (token == null || storedHash == null) {
            log.error("Token verification failed: token or stored hash is null");
            throw new IllegalArgumentException("Token and hash cannot be null");
        }
        
        String computedHash = hashToken(token);
        // Use MessageDigest.isEqual to prevent timing attacks
        boolean isValid = MessageDigest.isEqual(
            computedHash.getBytes(StandardCharsets.UTF_8),
            storedHash.getBytes(StandardCharsets.UTF_8)
        );
        
        if (isValid) {
            log.debug("Token verification successful");
        } else {
            log.warn("Token verification failed: invalid token");
        }
        
        return isValid;
    }
}