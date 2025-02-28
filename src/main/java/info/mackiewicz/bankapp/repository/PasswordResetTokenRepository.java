package info.mackiewicz.bankapp.repository;

import info.mackiewicz.bankapp.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing password reset tokens
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    /**
     * Find token by its hashed value
     */
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
    
    /**
     * Find active tokens for a user.
     * Used for validating token limits per user.
     */
    @Query("SELECT t FROM PasswordResetToken t WHERE t.userEmail = :userEmail AND t.used = false AND t.expiresAt > :now")
    List<PasswordResetToken> findValidTokensByUserEmail(@Param("userEmail") String userEmail, @Param("now") LocalDateTime now);

    /**
     * Count active tokens for a user.
     * Used for rate limiting - preventing users from requesting too many tokens.
     */
    @Query("SELECT COUNT(t) FROM PasswordResetToken t WHERE t.userEmail = :userEmail AND t.used = false AND t.expiresAt > :now")
    long countValidTokensByUserEmail(@Param("userEmail") String userEmail, @Param("now") LocalDateTime now);

    /**
     * Find all active tokens.
     * Used for system monitoring and potential security alerts.
     */
    @Query("SELECT t FROM PasswordResetToken t WHERE t.used = false AND t.expiresAt > :now")
    List<PasswordResetToken> findAllValidTokens(@Param("now") LocalDateTime now);

    /**
     * Find expired tokens.
     * Used by cleanup job to remove old tokens.
     */
    @Query("SELECT t FROM PasswordResetToken t WHERE t.expiresAt <= :now")
    List<PasswordResetToken> findExpiredTokens(@Param("now") LocalDateTime now);

        /**
     * Delete tokens older than specified date.
     * Used for database cleanup of old tokens.
     */
    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiresAt <= :cutoffDate")
    int deleteTokensOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);
}
