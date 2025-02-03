package BankApp.model;

import BankApp.controller.TransactionExecutor;
import BankApp.strategy.TransactionStrategy;

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

    public void generateRandomTransactions(TransactionType type, TransactionStrategy strategy, AccountService accountService, int count) {
        for (int i = 0; i < count; i++) {
            Transaction transaction = createRandomTransaction(type, strategy, accountService);
            futures.add(executor.submit(new TransactionExecutor(transaction)));
        }
    }

    private Transaction createRandomTransaction(TransactionType type, TransactionStrategy strategy, AccountService accountService) {
        Account acc1 = accountService.getRandomAccount();
        double amount = getRandomTransactionAmount(1000);

        if (type == TransactionType.TRANSFER) {
            Account acc2 = getDifferentRandomAccount(acc1, accountService);
            return new Transaction(acc1, acc2, amount, type, strategy);
        } else {
            return new Transaction(acc1, amount, type, strategy);
        }
    }

    private double getRandomTransactionAmount(double max) {
        double rawAmount = random.nextDouble(max);
        return Math.round(rawAmount * 100.0) / 100.0;
    }

    private Account getDifferentRandomAccount(Account account, AccountService accountService) {
        Account candidate = accountService.getRandomAccount();
        while (account.equals(candidate)) {
            candidate = accountService.getRandomAccount();
        }
        return candidate;
    }




    public void waitForAllTransactions() {
        for (Future<?> future : futures) {
            try {
                future.get();
                shutdownExecutors();
            } catch (InterruptedException e) {
                System.out.println("Interrupted while waiting for transactions");
            } catch (ExecutionException e) {
                System.out.println("Execution exception while waiting for transactions \n" + e.getMessage());
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