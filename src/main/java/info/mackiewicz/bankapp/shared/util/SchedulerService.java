package info.mackiewicz.bankapp.shared.util;

import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.security.service.PasswordResetTokenService;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SchedulerService {

    private final TransactionService transactionService;
    private final PasswordResetTokenService passwordResetTokenService;


    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedRate = 10)
    public void scheduleProcessAllNewTransactions() {
        log.debug("Scheduler: Processing all new transactions");
        transactionService.processAllNewTransactions();
        log.debug("Scheduler: All new transactions processed");
    }
    @Scheduled(timeUnit = TimeUnit.DAYS, fixedRate = 1)
    public void scheduleCleanupOldPasswordResetTokens() {
        log.debug("Scheduler: Cleaning up old password reset tokens");
        passwordResetTokenService.cleanupOldTokens();
        log.debug("Scheduler: Old password reset tokens cleaned up");
    }
}