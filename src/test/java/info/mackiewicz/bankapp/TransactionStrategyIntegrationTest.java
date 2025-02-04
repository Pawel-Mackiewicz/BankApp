package info.mackiewicz.bankapp;

import info.mackiewicz.bankapp.model.Account;
import info.mackiewicz.bankapp.model.Person;
import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.model.TransactionType;
import info.mackiewicz.bankapp.service.AccountService;
import info.mackiewicz.bankapp.service.PersonService;
import info.mackiewicz.bankapp.strategy.DepositTransaction;
import info.mackiewicz.bankapp.strategy.FeeTransaction;
import info.mackiewicz.bankapp.strategy.TransferTransaction;
import info.mackiewicz.bankapp.strategy.WithdrawalTransaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TransactionStrategyIntegrationTest {

    @Autowired
    private PersonService personService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private DepositTransaction depositTransaction;

    @Autowired
    private WithdrawalTransaction withdrawalTransaction;

    @Autowired
    private TransferTransaction transferTransaction;

    @Autowired
    private FeeTransaction feeTransaction;

    /**
     * Test the DepositTransaction strategy.
     * This test creates a person and an account, then uses the deposit strategy
     * to add 500.00 to the account. The test verifies that the updated balance is correct.
     */
    @Test
    public void testDepositTransactionStrategy() {
        // Create and persist a Person
        Person person = new Person();
        person.setName("Alice");
        person.setLastname("Anderson");
        person.setDateOfBirth(LocalDate.of(1990, 1, 1));
        person.setPESEL("11111111111");
        person = personService.createPerson(person);

        // Create an account for the person
        Account account = accountService.createAccount(person);
        // Verify initial balance is 0.00
        assertEquals(BigDecimal.ZERO, account.getBalance(), "Initial balance should be zero");

        // Create a deposit transaction to deposit 500.00 into the account
        Transaction depositTx = new Transaction(
                account,                         // deposit target account
                new BigDecimal("500.00"),        // amount
                TransactionType.DEPOSIT,         // type is DEPOSIT
                depositTransaction               // strategy to perform deposit
        );

        // Execute the deposit strategy
        boolean result = depositTransaction.execute(depositTx);
        assertTrue(result, "Deposit strategy should execute successfully");

        // Retrieve the updated account and verify the balance is 500.00
        Account updatedAccount = accountService.getAccountById(account.getId());
        assertEquals(new BigDecimal("500.00"), updatedAccount.getBalance(), "Balance should be 500.00 after deposit");
    }

    /**
     * Test the WithdrawalTransaction strategy.
     * Creates a person and account, deposits 1000.00, then uses the withdrawal strategy
     * to withdraw 300.00. It verifies the final balance is 700.00.
     */
    @Test
    public void testWithdrawalTransactionStrategy() {
        // Create and persist a Person
        Person person = new Person();
        person.setName("Bob");
        person.setLastname("Brown");
        person.setDateOfBirth(LocalDate.of(1991, 2, 2));
        person.setPESEL("22222222222");
        person = personService.createPerson(person);

        // Create an account and deposit 1000.00
        Account account = accountService.createAccount(person);
        account = accountService.deposit(account.getId(), new BigDecimal("1000.00"));
        assertEquals(new BigDecimal("1000.00"), account.getBalance(), "Balance should be 1000.00 after deposit");

        // Create a withdrawal transaction to withdraw 300.00 from the account
        Transaction withdrawalTx = new Transaction(
                account,                           // withdrawal source account
                new BigDecimal("300.00"),          // amount
                TransactionType.WITHDRAWAL,        // type is WITHDRAWAL
                withdrawalTransaction              // strategy to perform withdrawal
        );

        // Execute the withdrawal strategy
        boolean result = withdrawalTransaction.execute(withdrawalTx);
        assertTrue(result, "Withdrawal strategy should execute successfully");

        // Verify that the final balance is 700.00 (1000 - 300)
        Account updatedAccount = accountService.getAccountById(account.getId());
        assertEquals(new BigDecimal("700.00"), updatedAccount.getBalance(), "Balance should be 700.00 after withdrawal");
    }

    /**
     * Test the TransferTransaction strategy.
     * Creates two persons with accounts, deposits funds (1000.00 and 500.00), then uses the transfer strategy
     * to move 200.00 from the first account to the second account. Verifies final balances.
     */
    @Test
    public void testTransferTransactionStrategy() {
        // Create first person and account; deposit 1000.00
        Person person1 = new Person();
        person1.setName("Charlie");
        person1.setLastname("Clark");
        person1.setDateOfBirth(LocalDate.of(1992, 3, 3));
        person1.setPESEL("33333333333");
        person1 = personService.createPerson(person1);
        Account account1 = accountService.createAccount(person1);
        account1 = accountService.deposit(account1.getId(), new BigDecimal("1000.00"));

        // Create second person and account; deposit 500.00
        Person person2 = new Person();
        person2.setName("Diana");
        person2.setLastname("Davis");
        person2.setDateOfBirth(LocalDate.of(1993, 4, 4));
        person2.setPESEL("44444444444");
        person2 = personService.createPerson(person2);
        Account account2 = accountService.createAccount(person2);
        account2 = accountService.deposit(account2.getId(), new BigDecimal("500.00"));

        // Create a transfer transaction: transfer 200.00 from account1 to account2
        Transaction transferTx = new Transaction(
                account1,                          // source account
                account2,                          // destination account
                new BigDecimal("200.00"),          // transfer amount
                TransactionType.TRANSFER,          // type is TRANSFER
                transferTransaction                // strategy to perform transfer
        );

        // Execute the transfer strategy
        boolean result = transferTransaction.execute(transferTx);
        assertTrue(result, "Transfer strategy should execute successfully");

        // Retrieve updated accounts and verify final balances:
        // account1 should have 1000.00 - 200.00 = 800.00
        // account2 should have 500.00 + 200.00 = 700.00
        Account updatedAccount1 = accountService.getAccountById(account1.getId());
        Account updatedAccount2 = accountService.getAccountById(account2.getId());
        assertEquals(new BigDecimal("800.00"), updatedAccount1.getBalance(), "Account1 balance should be 800.00 after transfer");
        assertEquals(new BigDecimal("700.00"), updatedAccount2.getBalance(), "Account2 balance should be 700.00 after transfer");
    }

    /**
     * Test the FeeTransaction strategy.
     * Creates a person and an account, deposits 1000.00, then applies a fee transaction that withdraws 50.00.
     * Verifies that the final balance is 950.00.
     */
    @Test
    public void testFeeTransactionStrategy() {
        // Create and persist a Person
        Person person = new Person();
        person.setName("Eve");
        person.setLastname("Evans");
        person.setDateOfBirth(LocalDate.of(1994, 5, 5));
        person.setPESEL("55555555555");
        person = personService.createPerson(person);

        // Create an account and deposit 1000.00
        Account account = accountService.createAccount(person);
        account = accountService.deposit(account.getId(), new BigDecimal("1000.00"));
        assertEquals(new BigDecimal("1000.00"), account.getBalance(), "Balance should be 1000.00 after deposit");

        // Create a fee transaction to deduct 50.00 from the account
        Transaction feeTx = new Transaction(
                account,                           // fee applies to this account
                new BigDecimal("50.00"),           // fee amount
                TransactionType.FEE,               // type is FEE
                feeTransaction                     // strategy to perform fee deduction
        );

        // Execute the fee transaction strategy
        boolean result = feeTransaction.execute(feeTx);
        assertTrue(result, "Fee transaction should execute successfully");

        // Verify that the final balance is 950.00 (1000.00 - 50.00)
        Account updatedAccount = accountService.getAccountById(account.getId());
        assertEquals(new BigDecimal("950.00"), updatedAccount.getBalance(), "Balance should be 950.00 after fee transaction");
    }
}
