package info.mackiewicz.bankapp.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SchedulerService {

    private final TransactionService transactionService;


    @Scheduled(fixedRate = 600000)
    public void scheduleProcessAllNewTransactions() {
        transactionService.processAllNewTransactions();
    }

}