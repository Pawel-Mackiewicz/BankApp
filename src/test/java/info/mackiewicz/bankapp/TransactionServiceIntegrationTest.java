package info.mackiewicz.bankapp;

import info.mackiewicz.bankapp.model.Account;
import info.mackiewicz.bankapp.model.Person;
import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.model.TransactionType;
import info.mackiewicz.bankapp.service.AccountService;
import info.mackiewicz.bankapp.service.PersonService;
import info.mackiewicz.bankapp.service.TransactionService;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TransactionServiceIntegrationTest {

    @Autowired
    private PersonService personService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    /**
     * Test deposit transaction processing:
     * - Creates a person and account (initial balance = 0),
     * - Creates a deposit transaction for 500.00 (bez ręcznego hydratora),
     * - Persists and przetwarza transakcję przez processTransactionById(),
     * - Awaitility czeka aż saldo osiągnie 500.00.
     */
    @Test
    public void testDepositTransactionProcessing() {
        // Arrange: create person and account
        Person person = new Person();
        person.setName("Anna");
        person.setLastname("Kowalska");
        person.setDateOfBirth(LocalDate.of(1990, 1, 1));
        person.setPESEL("12345678901");
        person = personService.createPerson(person);

        Account account = accountService.createAccount(person);
        assertEquals(BigDecimal.ZERO, account.getBalance(), "Initial account balance should be zero.");

        // Act: create deposit transaction (strategy nie ustawione w konstruktorze)
        Transaction depositTx = new Transaction(account, new BigDecimal("500.00"), TransactionType.DEPOSIT);
        depositTx = transactionService.createTransaction(depositTx);

        // Process transaction by ID; TransactionProcessingService wewnętrznie wywoła hydrate()
        transactionService.processTransactionById(depositTx.getId());

        // Awaitility: wait until account balance becomes 500.00
        Awaitility.await().atMost(3, TimeUnit.SECONDS)
                .until(() -> accountService.getAccountById(account.getId())
                        .getBalance().compareTo(new BigDecimal("500.00")) == 0);

        // Assert
        Account updatedAccount = accountService.getAccountById(account.getId());
        assertEquals(new BigDecimal("500.00"), updatedAccount.getBalance(), "Account balance should be 500.00 after deposit transaction.");
    }

    /**
     * Test withdrawal transaction processing:
     * - Creates a person and account,
     * - Deposits 1000.00,
     * - Creates a withdrawal transaction for 300.00,
     * - Persists i przetwarza transakcję przez processTransaction(),
     * - Awaitility czeka, aż saldo konta wyniesie 700.00.
     */
    @Test
    public void testWithdrawalTransactionProcessing() {
        // Arrange: create person and account
        Person person = new Person();
        person.setName("Bartosz");
        person.setLastname("Nowak");
        person.setDateOfBirth(LocalDate.of(1985, 5, 5));
        person.setPESEL("23456789012");
        person = personService.createPerson(person);

        Account account = accountService.createAccount(person);
        account = accountService.deposit(account.getId(), new BigDecimal("1000.00"));
        assertEquals(new BigDecimal("1000.00"), account.getBalance(), "Account balance should be 1000.00 after deposit.");

        // Act: create withdrawal transaction for 300.00
        Transaction withdrawalTx = new Transaction(account, new BigDecimal("300.00"), TransactionType.WITHDRAWAL);
        withdrawalTx = transactionService.createTransaction(withdrawalTx);

        // Process transaction directly
        transactionService.processTransaction(withdrawalTx);

        // Awaitility: wait until balance becomes 700.00 (1000 - 300)
        Account finalAccount = account;
        Awaitility.await().atMost(3, TimeUnit.SECONDS)
                .until(() -> accountService.getAccountById(finalAccount.getId())
                        .getBalance().compareTo(new BigDecimal("700.00")) == 0);

        // Assert
        Account updatedAccount = accountService.getAccountById(account.getId());
        assertEquals(new BigDecimal("700.00"), updatedAccount.getBalance(), "Account balance should be 700.00 after withdrawal transaction.");
    }

    /**
     * Test transfer transaction processing:
     * - Creates two persons and two accounts,
     * - Deposits 1000.00 na pierwszym koncie i 500.00 na drugim,
     * - Creates a transfer transaction for 200.00 (używając konstruktora Transaction(from, to, amount, TRANSFER)),
     * - Persists and processes the transaction,
     * - Awaitility czeka, aż saldo pierwszego konta wyniesie 800.00, a drugiego 700.00.
     */
    @Test
    public void testTransferTransactionProcessing() {
        // Arrange: create person1 and account1
        Person person1 = new Person();
        person1.setName("Celina");
        person1.setLastname("Zielińska");
        person1.setDateOfBirth(LocalDate.of(1988, 3, 3));
        person1.setPESEL("34567890123");
        person1 = personService.createPerson(person1);
        Account account1 = accountService.createAccount(person1);
        account1 = accountService.deposit(account1.getId(), new BigDecimal("1000.00"));

        // Arrange: create person2 and account2
        Person person2 = new Person();
        person2.setName("Damian");
        person2.setLastname("Wiśniewski");
        person2.setDateOfBirth(LocalDate.of(1992, 7, 7));
        person2.setPESEL("45678901234");
        person2 = personService.createPerson(person2);
        Account account2 = accountService.createAccount(person2);
        account2 = accountService.deposit(account2.getId(), new BigDecimal("500.00"));

        // Act: create transfer transaction: 200.00 from account1 to account2
        Transaction transferTx = new Transaction(account1, account2, new BigDecimal("200.00"), TransactionType.TRANSFER);
        transferTx = transactionService.createTransaction(transferTx);

        // Process transaction by ID
        transactionService.processTransactionById(transferTx.getId());

        // Awaitility: wait until updated balances: account1 = 800.00 and account2 = 700.00
        Account finalAccount = account1;
        Account finalAccount1 = account2;
        Awaitility.await().atMost(3, TimeUnit.SECONDS)
                .until(() -> {
                    Account a1 = accountService.getAccountById(finalAccount.getId());
                    Account a2 = accountService.getAccountById(finalAccount1.getId());
                    return new BigDecimal("800.00").compareTo(a1.getBalance()) == 0 &&
                            new BigDecimal("700.00").compareTo(a2.getBalance()) == 0;
                });

        // Assert
        Account updated1 = accountService.getAccountById(account1.getId());
        Account updated2 = accountService.getAccountById(account2.getId());
        assertEquals(new BigDecimal("800.00"), updated1.getBalance(), "Account1 balance should be 800.00 after transfer.");
        assertEquals(new BigDecimal("700.00"), updated2.getBalance(), "Account2 balance should be 700.00 after transfer.");
    }

    /**
     * Test fee transaction processing:
     * - Creates a person and account,
     * - Deposits 1000.00,
     * - Creates a fee transaction for 50.00,
     * - Persists and processes the transaction,
     * - Awaitility czeka, aż saldo zostanie zaktualizowane do 950.00.
     */
    @Test
    public void testFeeTransactionProcessing() {
        // Arrange: create person and account
        Person person = new Person();
        person.setName("Ewa");
        person.setLastname("Nowicka");
        person.setDateOfBirth(LocalDate.of(1994, 5, 5));
        person.setPESEL("56789012345");
        person = personService.createPerson(person);

        Account account = accountService.createAccount(person);
        account = accountService.deposit(account.getId(), new BigDecimal("1000.00"));
        assertEquals(new BigDecimal("1000.00"), account.getBalance(), "Initial account balance should be 1000.00.");

        // Act: create fee transaction for 50.00
        Transaction feeTx = new Transaction(account, new BigDecimal("50.00"), TransactionType.FEE);
        feeTx = transactionService.createTransaction(feeTx);

        transactionService.processTransaction(feeTx);

        // Awaitility: wait until balance becomes 950.00 (1000 - 50)
        Account finalAccount = account;
        Awaitility.await().atMost(3, TimeUnit.SECONDS)
                .until(() -> accountService.getAccountById(finalAccount.getId())
                        .getBalance().compareTo(new BigDecimal("950.00")) == 0);

        // Assert
        Account updated = accountService.getAccountById(account.getId());
        assertEquals(new BigDecimal("950.00"), updated.getBalance(), "Account balance should be 950.00 after fee transaction.");
    }
}
