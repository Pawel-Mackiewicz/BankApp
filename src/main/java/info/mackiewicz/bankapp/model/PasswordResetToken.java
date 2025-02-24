package info.mackiewicz.bankapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity representing password reset token.
 *
 * Stores information about password reset requests and their status.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The token sent to user's email.
     */
    @Column(nullable = false, unique = true)
    private String token;

    /**
     * User who requested password reset.
     */
    @Column(name = "user_email", nullable = false)
    private String userEmail;

    /**
     * Date and time when the token expires.
     * Tokens are valid for a limited time (e.g., 1 hour).
     */
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    /**
     * Indicates whether the token has been used.
     * Once used, a token cannot be used again.
     */
    @Column(nullable = false)
    private boolean used;

    /**
     * Date and time when the token was used.
     * Useful for auditing and security monitoring.
     */
    @Column
    private LocalDateTime usedAt;

    /**
     * Creates a new password reset token for the given user
     * @param token The token value
     * @param user The user requesting password reset
     */
    public PasswordResetToken(String token, String userEmail) {
        this.token = token;
        this.userEmail = userEmail;
        this.expiresAt = LocalDateTime.now().plusMinutes(60);
        this.used = false;
    }

    /**
     * Checks if the token has expired
     * @return true if the current time is after expiration time
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Checks if the token is valid for use
     * @return true if the token is not expired and has not been used
     */
    public boolean isValid() {
        return !isExpired() && !used;
    }

    /**
     * Marks the token as used with the current timestamp
     */
    public void markAsUsed() {
        if (!isValid()) {
            throw new IllegalStateException("Token is not valid");
        }
        this.used = true;
        this.usedAt = LocalDateTime.now();
    }

    /**
     * Gets time until token expiration
     * @return remaining minutes until expiration
     */
    public long getMinutesUntilExpiration() {
        return java.time.Duration.between(LocalDateTime.now(), expiresAt).toMinutes();
    }
}