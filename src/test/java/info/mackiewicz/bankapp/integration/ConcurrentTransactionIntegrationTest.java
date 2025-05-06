package info.mackiewicz.bankapp.integration;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.account.service.AccountService;
import info.mackiewicz.bankapp.core.transaction.exception.InsufficientFundsException;
import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import info.mackiewicz.bankapp.core.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.core.transaction.service.TransactionService;
import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.core.user.service.UserService;
import info.mackiewicz.bankapp.integration.utils.IntegrationTestAccountService;
import info.mackiewicz.bankapp.integration.utils.IntegrationTestConfig;
import info.mackiewicz.bankapp.integration.utils.IntegrationTestUserService;
import info.mackiewicz.bankapp.system.transaction.processing.locking.LockingConfig;
import info.mackiewicz.bankapp.system.transaction.processing.TransactionProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@Import(IntegrationTestConfig.class)
@EnableConfigurationProperties(LockingConfig.class)
@DisplayName("Concurrent Transactions Integration Tests")
class ConcurrentTransactionIntegrationTest {

    @Autowired
    private IntegrationTestUserService testUserService;

    @Autowired
    private IntegrationTestAccountService testAccountService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionProcessingService transactionProcessingService;

    @Autowired
    private UserService userService;

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
        for (int i = 0; i < 20; i++) {
            User user = testUserService.createRandomTestUser();
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
        log.info("Starting concurrent transactions test with ID: {}", testRunId);
        int numberOfTransactions = 100;
        List<Transaction> transactions = new ArrayList<>();

        for (int i = 0; i < numberOfTransactions; i++) {
            Transaction transaction = createRandomTransfer();
            transactions.add(transaction);
        }

        transactionProcessingService.processAllNewTransactions();

        await()
                .atMost(Duration.ofSeconds(60))
                .untilAsserted(() -> {
                    verifyTransactionResults(transactions);
                    verifySystemBalance();
                });
    }

    @Test
    @DisplayName("Should handle multiple withdrawals from same account correctly")
    void testMultipleWithdrawals() {
        log.info("Starting multiple withdrawals test with ID: {}", testRunId);
        int numberOfWithdrawals = 20;
        Account sourceAccount = testAccounts.getFirst();
        BigDecimal initialBalance = sourceAccount.getBalance();
        BigDecimal withdrawalAmount = initialBalance.divide(BigDecimal.valueOf(numberOfWithdrawals * 2), 2, RoundingMode.HALF_UP);

        for (int i = 0; i < numberOfWithdrawals; i++) {
            createWithdrawal(sourceAccount, withdrawalAmount);
        }

        transactionProcessingService.processAllNewTransactions();

        await()
                .atMost(Duration.ofSeconds(15))
                .untilAsserted(() -> {
                    List<Transaction> transactions = transactionService.getTransactionsByAccountId(sourceAccount.getId());
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
        log.info("Starting parallel deposits test with ID: {}", testRunId);
        int numberOfDeposits = 20;
        Account destinationAccount = testAccounts.getFirst();
        BigDecimal initialBalance = destinationAccount.getBalance();
        BigDecimal depositAmount = BigDecimal.valueOf(100);

        List<Transaction> transactions = new ArrayList<>();

        for (int i = 0; i < numberOfDeposits; i++) {
            Transaction deposit = createDeposit(destinationAccount, depositAmount);
            transactions.add(deposit);
        }

        transactionProcessingService.processAllNewTransactions();

        await()
                .atMost(Duration.ofSeconds(15))
                .untilAsserted(() -> {
                    List<Transaction> completedTransactions = transactionService.getTransactionsByAccountId(destinationAccount.getId());
                    Account refreshed = accountService.getAccountById(destinationAccount.getId());
                    BigDecimal expectedBalance = initialBalance.add(
                            depositAmount.multiply(BigDecimal.valueOf(
                                    completedTransactions.stream()
                                            .filter(t -> t.getStatus() == TransactionStatus.DONE)
                                            .count()
                            ))
                    );

                    assertThat(refreshed.getBalance())
                            .as("Account balance should reflect successful deposits only")
                            .isEqualByComparingTo(expectedBalance);
                });
    }

    @Test
    @DisplayName("Should handle chain transfers (A→B→C) correctly")
    void testChainTransfers() {
        log.info("Starting chain transfers test with ID: {}", testRunId);
        int numberOfChains = 20;
        List<Transaction> transactions = new ArrayList<>();

        for (int i = 0; i < numberOfChains; i++) {
            Account accountA = testAccounts.get(i * 3 % testAccounts.size());
            Account accountB = testAccounts.get((i * 3 + 1) % testAccounts.size());
            Account accountC = testAccounts.get((i * 3 + 2) % testAccounts.size());

            BigDecimal amount = BigDecimal.valueOf(100);

            Transaction t1 = createTransfer(accountA, accountB, amount);
            Transaction t2 = createTransfer(accountB, accountC, amount);

            transactions.add(t1);
            transactions.add(t2);
        }

        transactionProcessingService.processAllNewTransactions();

        await()
                .atMost(Duration.ofSeconds(15))
                .untilAsserted(() -> {
                    verifyTransactionResults(transactions);
                    verifySystemBalance();
                });
    }

    @Test
    @DisplayName("Should handle bidirectional transfers correctly")
    void testBidirectionalTransfers() {
        log.info("Starting bidirectional transfers test with ID: {}", testRunId);
        int numberOfPairs = 20;
        List<Transaction> transactions = new ArrayList<>();
        // Store initial balances for all accounts that will participate in transfers
        Map<Integer, BigDecimal> initialBalances = new HashMap<>();

        for (int i = 0; i < numberOfPairs; i++) {
            Account accountA = testAccounts.get(i * 2 % testAccounts.size());
            Account accountB = testAccounts.get((i * 2 + 1) % testAccounts.size());
            BigDecimal amount = BigDecimal.valueOf(100);

            // Store initial balances if not already stored
            initialBalances.putIfAbsent(accountA.getId(), accountA.getBalance());
            initialBalances.putIfAbsent(accountB.getId(), accountB.getBalance());

            // Create bidirectional transfers
            Transaction t1 = createTransfer(accountA, accountB, amount);
            Transaction t2 = createTransfer(accountB, accountA, amount);

            transactions.add(t1);
            transactions.add(t2);
        }

        transactionProcessingService.processAllNewTransactions();

        await()
                .atMost(Duration.ofSeconds(30))
                .untilAsserted(() -> {
                    verifyTransactionResults(transactions);

                    // Verify all accounts have their original balance
                    for (Account account : testAccounts) {
                        if (initialBalances.containsKey(account.getId())) {
                            BigDecimal currentBalance = accountService.getAccountById(account.getId()).getBalance();
                            BigDecimal expectedBalance = initialBalances.get(account.getId());
                            assertThat(currentBalance)
                                    .as("Account %d balance should equal its initial balance of %s after bidirectional transfers",
                                            account.getId(), expectedBalance)
                                    .isEqualByComparingTo(expectedBalance);
                        }
                    }

                    verifySystemBalance();
                });
    }

    @Test
    @DisplayName("Should handle circular chain transfers (A→B→C→A) correctly")
    void testCircularChainTransfers() {
        log.info("Starting circular chain transfers test with ID: {}", testRunId);
        int numberOfChains = 20;
        List<Transaction> transactions = new ArrayList<>();
        BigDecimal transferAmount = BigDecimal.valueOf(100);
        // Store initial balances for all accounts that will participate in transfers
        Map<Integer, BigDecimal> initialBalances = new HashMap<>();

        for (int i = 0; i < numberOfChains; i++) {
            Account accountA = testAccounts.get(i * 3 % testAccounts.size());
            Account accountB = testAccounts.get((i * 3 + 1) % testAccounts.size());
            Account accountC = testAccounts.get((i * 3 + 2) % testAccounts.size());

            // Store initial balances if not already stored
            initialBalances.putIfAbsent(accountA.getId(), accountA.getBalance());
            initialBalances.putIfAbsent(accountB.getId(), accountB.getBalance());
            initialBalances.putIfAbsent(accountC.getId(), accountC.getBalance());

            Transaction t1 = createTransfer(accountA, accountB, transferAmount);
            Transaction t2 = createTransfer(accountB, accountC, transferAmount);
            Transaction t3 = createTransfer(accountC, accountA, transferAmount);

            transactions.add(t1);
            transactions.add(t2);
            transactions.add(t3);
        }

        transactionProcessingService.processAllNewTransactions();

        await()
                .atMost(Duration.ofSeconds(15))
                .untilAsserted(() -> {
                    verifyTransactionResults(transactions);

                    // Verify each account's balance returned to initial state
                    for (Account account : testAccounts) {
                        if (initialBalances.containsKey(account.getId())) {
                            BigDecimal currentBalance = accountService.getAccountById(account.getId()).getBalance();
                            BigDecimal expectedBalance = initialBalances.get(account.getId());
                            assertThat(currentBalance)
                                    .as("Account %d balance should equal its initial balance of %s after circular transfers",
                                            account.getId(), expectedBalance)
                                    .isEqualByComparingTo(expectedBalance);
                        }
                    }

                    verifySystemBalance();
                });
    }

    @Test
    @DisplayName("Should handle edge cases correctly")
    void testEdgeCases() {
        log.info("Starting edge cases test with ID: {}", testRunId);
        // Test zero balance account
        Account zeroBalanceAccount = testAccounts.get(0);
        accountService.withdraw(zeroBalanceAccount, zeroBalanceAccount.getBalance()); // Set balance to zero

        // Attempt transfer from zero balance account - should fail immediately
        assertThrows(InsufficientFundsException.class, () -> {
            Transaction transfer = Transaction.buildTransfer()
                    .from(zeroBalanceAccount)
                    .to(testAccounts.get(1))
                    .withAmount(BigDecimal.TEN)
                    .withTitle("Test zero balance transfer")
                    .build();
            transactionService.registerTransaction(transfer);
        }, "Should throw InsufficientFundsException when trying to create transfer with insufficient funds");
    }

    private Account createTestAccount(User user) {
        BigDecimal initialBalance = BigDecimal.valueOf(random.nextInt(9000) + 1000);
        return testAccountService.createTestAccountWithBalance(user.getId(), initialBalance);
    }

    private Transaction createRandomTransfer() {
        Account sourceAccount = testAccounts.get(random.nextInt(testAccounts.size()));
        Account destinationAccount;
        do {
            destinationAccount = testAccounts.get(random.nextInt(testAccounts.size()));
        } while (destinationAccount.equals(sourceAccount));

        BigDecimal maxAmount = sourceAccount.getBalance().multiply(BigDecimal.valueOf(0.3));
        BigDecimal amount = BigDecimal.valueOf(random.nextDouble() * maxAmount.doubleValue())
                .setScale(2, RoundingMode.HALF_UP);

        return createTransfer(sourceAccount, destinationAccount, amount);
    }

    private Transaction createTransfer(Account source, Account destination, BigDecimal amount) {
        Transaction transaction = Transaction.buildTransfer()
                .from(source)
                .to(destination)
                .withAmount(amount)
                .withTitle("Test concurrent transfer")
                .build();

        return transactionService.registerTransaction(transaction);
    }

    private Transaction createWithdrawal(Account account, BigDecimal amount) {
        Transaction transaction = Transaction.buildWithdrawal()
                .from(account)
                .withAmount(amount)
                .withTitle("Test concurrent withdrawal")
                .build();
        return transactionService.registerTransaction(transaction);
    }

    private Transaction createDeposit(Account account, BigDecimal amount) {
        Transaction transaction = Transaction.buildDeposit()
                .to(account)
                .withAmount(amount)
                .withTitle("Test concurrent deposit")
                .build();
        return transactionService.registerTransaction(transaction);
    }

    private void verifyTransactionResults(List<Transaction> transactions) {
        transactions.forEach(transaction -> {
            Transaction completed = transactionService.getTransactionById(transaction.getId());
            assertThat(completed.getStatus())
                    .as("Transaction %d should be completed", completed.getId())
                    .isIn(TransactionStatus.DONE, TransactionStatus.INSUFFICIENT_FUNDS);
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