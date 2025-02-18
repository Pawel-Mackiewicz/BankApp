package info.mackiewicz.bankapp.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SchedulerService {
    private final TransactionService transactionService;

    public SchedulerService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Scheduled(fixedRate = 60000)
    public void scheduleProcessAllNewTransactions() {
        transactionService.processAllNewTransactions();
    }

}