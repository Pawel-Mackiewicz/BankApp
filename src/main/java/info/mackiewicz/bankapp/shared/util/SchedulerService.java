package info.mackiewicz.bankapp.shared.util;

import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.security.service.PasswordResetTokenService;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SchedulerService {

    private final TransactionService transactionService;
    private final PasswordResetTokenService passwordResetTokenService;


    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedRate = 10)
    public void scheduleProcessAllNewTransactions() {
        transactionService.processAllNewTransactions();
    }
    @Scheduled(timeUnit = TimeUnit.DAYS, fixedRate = 1)
    public void scheduleCleanupOldPasswordResetTokens() {
        passwordResetTokenService.cleanupOldTokens();
    }
}