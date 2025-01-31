package BankApp.model;

import BankApp.controller.TransactionExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class TransactionService {
    ExecutorService executor;

    private final List<Future<?>> futures;
    private final List<TransactionExecutor> transactionExecutors;
    private final Random random;

    public TransactionService() {
        executor = Executors.newCachedThreadPool();
        futures = new ArrayList<>();

        this.transactionExecutors = new ArrayList<>();
        this.random = new Random();
    }

    public void generateRandomTransaction(TransactionType type, AccountService accountService, int count) {
        for (int i = 0; i < count; i++) {
            Account acc1 = null;
            Account acc2 = null;
            do {
                acc1 = accountService.getRandomAccount();
                acc2 = type == TransactionType.TRANSFER ? accountService.getRandomAccount() : null;
            } while (acc1 == acc2);

            Transaction transaction = acc2 != null ? new Transaction(acc1, acc2, random.nextInt(1000))
                    : new Transaction(type, acc1, random.nextInt(1000));

            futures.add(executor.submit(new TransactionExecutor(transaction)));
        }
    }

    public void waitForAllTransactions() {
        for (Future<?> future : futures) {
            try {
                future.get();
                shutdownExecutors();
            } catch (InterruptedException e) {
                System.out.println("Interrupted while waiting for transactions");
            } catch (ExecutionException e) {
                System.out.println("Execution exception while waiting for transactions");
            }
        }
    }

    public void shutdownExecutors() {
        executor.shutdown();
        try {
            if (executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupted while shutting down executors");
        }
    }
}