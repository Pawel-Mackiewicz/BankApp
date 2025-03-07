package info.mackiewicz.bankapp.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ActiveProfiles;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionBuilder;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.service.UserService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class ConcurrentTransactionIntegrationTest {

    @TestConfiguration
    static class TestConfig {
        @Bean(name = "testExecutor")
        public Executor testExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(10);
            executor.setMaxPoolSize(50);
            executor.setQueueCapacity(200);
            executor.setThreadNamePrefix("TestAsync-");
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            executor.initialize();
            return executor;
        }
    }

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionBuilder transactionBuilder;

    @Autowired
    private UserService userService;

    @Autowired
    private Executor testExecutor;

    private List<Account> testAccounts;
    private List<User> testUsers;
    private BigDecimal totalInitialBalance;
    private final Random random = new Random();
    private final String testRunId = UUID.randomUUID().toString().substring(0, 8);

    @BeforeEach
    void setup() {
        testAccounts = new ArrayList<>();
        testUsers = new ArrayList<>();
        totalInitialBalance = BigDecimal.ZERO;

        // Create test users (we need separate users as there's a 3 account limit per user)
        for (int i = 0; i < 10; i++) {
            User user = createTestUser(i);
            testUsers.add(user);
            
            // Create one account per user
            Account account = createTestAccount(user);
            testAccounts.add(account);
            totalInitialBalance = totalInitialBalance.add(account.getBalance());
        }

        log.info("Test setup completed. Created {} accounts with total balance: {}", 
            testAccounts.size(), totalInitialBalance);
    }

    @Test
    @DisplayName("Should handle multiple concurrent transactions correctly")
    void testConcurrentTransactions() {
        int numberOfTransactions = 20;
        List<CompletableFuture<Void>> futures = new CopyOnWriteArrayList<>();
        List<Transaction> transactions = new CopyOnWriteArrayList<>();

        for (int i = 0; i < numberOfTransactions; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                Transaction transaction = createRandomTransfer();
                transactions.add(transaction);
                transactionService.processTransactionById(transaction.getId());
            }, testExecutor);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .join();

        await()
            .atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> {
                verifyTransactionResults(transactions);
                verifySystemBalance();
            });
    }

    @Test
    @DisplayName("Should handle multiple withdrawals from same account correctly")
    void testMultipleWithdrawals() {
        Account sourceAccount = testAccounts.get(0);
        BigDecimal initialBalance = sourceAccount.getBalance();
        int numberOfWithdrawals = 10;
        BigDecimal withdrawalAmount = initialBalance.divide(BigDecimal.valueOf(numberOfWithdrawals * 2));
        
        List<CompletableFuture<Void>> futures = new CopyOnWriteArrayList<>();
        List<Transaction> transactions = new CopyOnWriteArrayList<>();

        for (int i = 0; i < numberOfWithdrawals; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                Transaction withdrawal = createWithdrawal(sourceAccount, withdrawalAmount);
                transactions.add(withdrawal);
                transactionService.processTransactionById(withdrawal.getId());
                log.debug("INSIDE OF PĘTLA: Processed withdrawal: {}", withdrawal.getId());
            }, testExecutor);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        log.debug("OUTSIDE OF PĘTLA: All withdrawals submitted");

        await()
            .atMost(Duration.ofSeconds(15))
            .untilAsserted(() -> {
                Account refreshed = accountService.getAccountById(sourceAccount.getId());
                BigDecimal expectedBalance = initialBalance.subtract(
                    withdrawalAmount.multiply(BigDecimal.valueOf(
                        transactions.stream()
                            .filter(t -> t.getStatus() == TransactionStatus.DONE)
                            .count()
                    ))
                );
                assertThat(refreshed.getBalance())
                    .as("Account balance should reflect successful withdrawals only")
                    .isEqualByComparingTo(expectedBalance);
            });
    }

    @Test
    @DisplayName("Should handle parallel deposits to same account correctly")
    void testParallelDeposits() {
        Account destinationAccount = testAccounts.get(0);
        BigDecimal initialBalance = destinationAccount.getBalance();
        int numberOfDeposits = 15;
        BigDecimal depositAmount = BigDecimal.valueOf(100);
        
        List<CompletableFuture<Void>> futures = new CopyOnWriteArrayList<>();
        List<Transaction> transactions = new CopyOnWriteArrayList<>();

        for (int i = 0; i < numberOfDeposits; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                Transaction deposit = createDeposit(destinationAccount, depositAmount);
                transactions.add(deposit);
                transactionService.processTransactionById(deposit.getId());
            }, testExecutor);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        await()
            .atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> {
                Account refreshed = accountService.getAccountById(destinationAccount.getId());
                BigDecimal expectedBalance = initialBalance.add(
                    depositAmount.multiply(BigDecimal.valueOf(numberOfDeposits))
                );
                assertThat(refreshed.getBalance())
                    .as("Account balance should reflect all deposits")
                    .isEqualByComparingTo(expectedBalance);
            });
    }

    @Test
    @DisplayName("Should handle chain transfers (A→B→C) correctly")
    void testChainTransfers() {
        int numberOfChains = 5;
        List<CompletableFuture<Void>> futures = new CopyOnWriteArrayList<>();
        List<Transaction> transactions = new CopyOnWriteArrayList<>();

        for (int i = 0; i < numberOfChains; i++) {
            final int chainIndex = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                Account accountA = testAccounts.get(chainIndex * 3 % testAccounts.size());
                Account accountB = testAccounts.get((chainIndex * 3 + 1) % testAccounts.size());
                Account accountC = testAccounts.get((chainIndex * 3 + 2) % testAccounts.size());
                
                BigDecimal amount = BigDecimal.valueOf(100);
                
                Transaction t1 = createTransfer(accountA, accountB, amount);
                Transaction t2 = createTransfer(accountB, accountC, amount);
                
                transactions.add(t1);
                transactions.add(t2);
                
                transactionService.processTransactionById(t1.getId());
                await()
                    .atMost(Duration.ofSeconds(30))
                    .until(() -> transactionService.getTransactionById(t1.getId()).getStatus() == TransactionStatus.DONE);
                    
                transactionService.processTransactionById(t2.getId());
            }, testExecutor);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        await()
            .atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> {
                verifyTransactionResults(transactions);
                verifySystemBalance();
            });
    }

    @Test
    @DisplayName("Should handle bidirectional transfers correctly")
    void testBidirectionalTransfers() {
        int numberOfPairs = 5;
        List<CompletableFuture<Void>> futures = new CopyOnWriteArrayList<>();
        List<Transaction> transactions = new CopyOnWriteArrayList<>();

        for (int i = 0; i < numberOfPairs; i++) {
            Account accountA = testAccounts.get(i * 2 % testAccounts.size());
            Account accountB = testAccounts.get((i * 2 + 1) % testAccounts.size());
            BigDecimal amount = BigDecimal.valueOf(100);

            CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
                Transaction t = createTransfer(accountA, accountB, amount);
                transactions.add(t);
                transactionService.processTransactionById(t.getId());
            }, testExecutor);

            CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
                Transaction t = createTransfer(accountB, accountA, amount);
                transactions.add(t);
                transactionService.processTransactionById(t.getId());
            }, testExecutor);

            futures.add(future1);
            futures.add(future2);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        await()
            .atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> {
                verifyTransactionResults(transactions);
                verifySystemBalance();
            });
    }

    @Test
    @DisplayName("Should handle edge cases correctly")
    void testEdgeCases() {
        // Test zero balance account
        Account zeroBalanceAccount = testAccounts.get(0);
        accountService.withdraw(zeroBalanceAccount, zeroBalanceAccount.getBalance()); // Set balance to zero

        // Attempt transfer from zero balance account
        Transaction zeroBalanceTransfer = createTransfer(
            zeroBalanceAccount,
            testAccounts.get(1),
            BigDecimal.TEN
        );
        transactionService.processTransactionById(zeroBalanceTransfer.getId());

        await()
            .atMost(Duration.ofSeconds(10))
            .untilAsserted(() -> {
                Transaction completed = transactionService.getTransactionById(zeroBalanceTransfer.getId());
                assertThat(completed.getStatus())
                    .as("Zero balance transfer should fail")
                    .isEqualTo(TransactionStatus.FAULTY);
            });

        // Test maximum concurrent transactions
        int maxTransactions = 100;
        List<CompletableFuture<Void>> futures = new CopyOnWriteArrayList<>();
        List<Transaction> transactions = new CopyOnWriteArrayList<>();

        for (int i = 0; i < maxTransactions; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                Transaction t = createRandomTransfer();
                transactions.add(t);
                transactionService.processTransactionById(t.getId());
            }, testExecutor);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        await()
            .atMost(Duration.ofSeconds(60))
            .untilAsserted(() -> {
                verifyTransactionResults(transactions);
                verifySystemBalance();
            });
    }

    private User createTestUser(int index) {
        User user = new User();
        String uniqueSuffix = testRunId + "-" + index;
        
        // Generate unique PESEL (needs to be 11 digits)
        // Use testRunId's hashCode to ensure uniqueness across test runs
        int hashCode = Math.abs(testRunId.hashCode());
        String uniqueSequence = String.format("%05d", (hashCode + index) % 100000);
        String yearMonth = "9901"; // Fixed birth year and month
        String day = String.format("%02d", (index % 28) + 1); // Day between 1-28
        String pesel = yearMonth + day + uniqueSequence;

        user.setPESEL(pesel);
        user.setFirstname("Test");
        user.setLastname("User" + uniqueSuffix);
        user.setEmail("test.user" + uniqueSuffix + "@test.com");
        user.setPassword("Password123!");
        user.setPhoneNumber("+48" + String.format("%09d", hashCode + index));
        user.setDateOfBirth(LocalDate.of(1999, 1, (index % 28) + 1)); // Match PESEL date
        return userService.createUser(user);
    }

    private Account createTestAccount(User user) {
        Account account = accountService.createAccount(user.getId());
        BigDecimal initialBalance = BigDecimal.valueOf(random.nextInt(9000) + 1000);
        accountService.deposit(account, initialBalance);
        return account;
    }

    private Transaction createRandomTransfer() {
        Account sourceAccount = testAccounts.get(random.nextInt(testAccounts.size()));
        Account destinationAccount;
        do {
            destinationAccount = testAccounts.get(random.nextInt(testAccounts.size()));
        } while (destinationAccount.equals(sourceAccount));

        BigDecimal maxAmount = sourceAccount.getBalance().multiply(BigDecimal.valueOf(0.9));
        BigDecimal amount = BigDecimal.valueOf(random.nextDouble() * maxAmount.doubleValue())
            .setScale(2, RoundingMode.HALF_UP);

        return createTransfer(sourceAccount, destinationAccount, amount);
    }

    private Transaction createTransfer(Account source, Account destination, BigDecimal amount) {
        Transaction transaction = transactionBuilder
            .withSourceAccount(source)
            .withDestinationAccount(destination)
            .withTransactionTitle("Test concurrent transfer")
            .withType(TransactionType.TRANSFER_INTERNAL)
            .withAmount(amount)
            .build();

        return transactionService.createTransaction(transaction);
    }

    private Transaction createWithdrawal(Account account, BigDecimal amount) {
        Transaction transaction = transactionBuilder
            .withSourceAccount(account)
            .withTransactionTitle("Test concurrent withdrawal")
            .withType(TransactionType.WITHDRAWAL)
            .withAmount(amount)
            .build();
        return transactionService.createTransaction(transaction);
    }

    private Transaction createDeposit(Account account, BigDecimal amount) {
        Transaction transaction = transactionBuilder
            .withDestinationAccount(account)
            .withTransactionTitle("Test concurrent deposit")
            .withType(TransactionType.DEPOSIT)
            .withAmount(amount)
            .build();
        return transactionService.createTransaction(transaction);
    }

    private void verifyTransactionResults(List<Transaction> transactions) {
        transactions.forEach(transaction -> {
            Transaction completed = transactionService.getTransactionById(transaction.getId());
            assertThat(completed.getStatus())
                .as("Transaction %d should be completed", completed.getId())
                .isIn(TransactionStatus.DONE, TransactionStatus.FAULTY);
        });
    }

    private void verifySystemBalance() {
        BigDecimal currentTotal = BigDecimal.ZERO;
        for (Account account : testAccounts) {
            Account refreshed = accountService.getAccountById(account.getId());
            currentTotal = currentTotal.add(refreshed.getBalance());
        }

        assertThat(currentTotal)
            .as("Total system balance should remain unchanged")
            .isEqualByComparingTo(totalInitialBalance);
    }

    @AfterEach
    void cleanup() {
        // First, delete all transactions
        testAccounts.forEach(account -> {
            try {
                List<Transaction> transactions = transactionService.getTransactionsByAccountId(account.getId());
                transactions.forEach(transaction ->
                    transactionService.deleteTransactionById(transaction.getId()));
            } catch (Exception e) {
                log.error("Error cleaning up transactions for account {}: {}",
                    account.getId(), e.getMessage());
            }
        });

        // Then delete accounts
        testAccounts.forEach(account -> {
            try {
                accountService.deleteAccountById(account.getId());
            } catch (Exception e) {
                log.error("Error cleaning up account {}: {}",
                    account.getId(), e.getMessage());
            }
        });
        
        // Finally delete users
        testUsers.forEach(user -> {
            try {
                userService.deleteUser(user.getId());
            } catch (Exception e) {
                log.error("Error cleaning up user {}: {}",
                    user.getId(), e.getMessage());
            }
        });
    }

}