package info.mackiewicz.bankapp.shared.util;

import info.mackiewicz.bankapp.system.security.recovery.password.service.PasswordResetTokenService;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Service responsible for scheduling and executing periodic tasks in the application.
 * This service manages scheduled tasks such as transaction processing and cleanup operations.
 * 
 * @see org.springframework.scheduling.annotation.Scheduled
 * @see info.mackiewicz.bankapp.transaction.service.TransactionService
 * @see PasswordResetTokenService
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SchedulerService {

    private final TransactionService transactionService;
    private final PasswordResetTokenService passwordResetTokenService;

    private final static int HOW_OFTEN_TO_PROCESS_NEW_TRANSACTIONS = 10; // in minutes
    private final static int HOW_OFTEN_TO_CLEANUP_OLD_PASSWORD_RESET_TOKENS = 1; // in days

    /**
     * Schedules periodic processing of all new transactions.
     * Executes every 10 minutes to handle any pending transactions in the system.
     * This method is thread-safe and runs automatically based on the configured schedule.
     * 
     * @throws RuntimeException if transaction processing fails
     * @see TransactionService#processAllNewTransactions()
     */
    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedRate = HOW_OFTEN_TO_PROCESS_NEW_TRANSACTIONS)
    public void scheduleProcessAllNewTransactions() {
        log.debug("Scheduler: Processing all new transactions");
        transactionService.processAllNewTransactions();
        log.debug("Scheduler: All new transactions processed");
    }

    /**
     * Schedules daily cleanup of expired password reset tokens.
     * Executes once per day to remove old password reset tokens from the system.
     * This helps maintain database cleanliness and security by removing unused tokens.
     * 
     * @throws RuntimeException if token cleanup operation fails
     * @see PasswordResetTokenService#cleanupOldTokens()
     */
    @Scheduled(timeUnit = TimeUnit.DAYS, fixedRate = HOW_OFTEN_TO_CLEANUP_OLD_PASSWORD_RESET_TOKENS)
    public void scheduleCleanupOldPasswordResetTokens() {
        log.debug("Scheduler: Cleaning up old password reset tokens");
        passwordResetTokenService.cleanupOldTokens();
        log.debug("Scheduler: Old password reset tokens cleaned up");
    }
}