package info.mackiewicz.bankapp;

import info.mackiewicz.bankapp.model.Account;
import info.mackiewicz.bankapp.model.Person;
import info.mackiewicz.bankapp.service.AccountService;
import info.mackiewicz.bankapp.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BankAppIntegrationTests {

    @Autowired
    private PersonService personService;

    @Autowired
    private AccountService accountService;

    // ------------------- PERSON TESTS -------------------
    // Test 1: Create a person and ensure an ID is assigned.
    @Test
    public void testCreatePerson() {
        Person person = new Person();
        person.setName("Alice");
        person.setLastname("Smith");
        person.setDateOfBirth(LocalDate.of(1990, 1, 1));
        person.setPESEL("11111111111");
        Person saved = personService.createPerson(person);
        assertNotNull(saved.getId(), "Saved person should have an ID");
    }

    // Test 2: Retrieve a person by ID.
    @Test
    public void testGetPersonById() {
        Person person = new Person();
        person.setName("Bob");
        person.setLastname("Jones");
        person.setDateOfBirth(LocalDate.of(1985, 5, 20));
        person.setPESEL("22222222222");
        Person saved = personService.createPerson(person);
        Optional<Person> found = personService.getPersonById(saved.getId());
        assertTrue(found.isPresent(), "Person should be found by ID");
        assertEquals("Bob", found.get().getName(), "Name should match");
    }

    // Test 3: Update a person's details.
    @Test
    public void testUpdatePerson() {
        Person person = new Person();
        person.setName("Carol");
        person.setLastname("Brown");
        person.setDateOfBirth(LocalDate.of(1970, 3, 15));
        person.setPESEL("33333333333");
        Person saved = personService.createPerson(person);
        saved.setLastname("White");
        Person updated = personService.updatePerson(saved);
        assertEquals("White", updated.getLastname(), "Lastname should be updated");
    }

    // Test 4: Delete a person and ensure they are no longer found.
    @Test
    public void testDeletePerson() {
        Person person = new Person();
        person.setName("Dave");
        person.setLastname("Black");
        person.setDateOfBirth(LocalDate.of(1995, 7, 10));
        person.setPESEL("44444444444");
        Person saved = personService.createPerson(person);
        Integer id = saved.getId();
        personService.deletePerson(id);
        Optional<Person> found = personService.getPersonById(id);
        assertFalse(found.isPresent(), "Deleted person should not be found");
    }

    // Test 5: Retrieve all persons.
    @Test
    public void testGetAllPersons() {
        int initialCount = personService.getAllPersons().size();
        Person p1 = new Person();
        p1.setName("Eve");
        p1.setLastname("Green");
        p1.setDateOfBirth(LocalDate.of(2000, 12, 12));
        p1.setPESEL("55555555555");
        personService.createPerson(p1);
        Person p2 = new Person();
        p2.setName("Frank");
        p2.setLastname("Blue");
        p2.setDateOfBirth(LocalDate.of(1992, 8, 8));
        p2.setPESEL("66666666666");
        personService.createPerson(p2);
        List<Person> persons = personService.getAllPersons();
        assertEquals(initialCount + 2, persons.size(), "Total persons should increase by 2");
    }

    // ------------------- ACCOUNT TESTS -------------------
    // Test 6: Create an account for a saved person.
    @Test
    public void testCreateAccount() {
        Person person = new Person();
        person.setName("Gina");
        person.setLastname("Gray");
        person.setDateOfBirth(LocalDate.of(1980, 2, 2));
        person.setPESEL("77777777777");
        person = personService.createPerson(person);
        Account account = accountService.createAccount(person);
        assertNotNull(account.getId(), "Account should have an ID after creation");
        // Assuming initial balance is 0.0
        assertEquals(BigDecimal.ZERO, account.getBalance(), "Initial balance should be zero");
    }

    // Test 7: Deposit funds into an account.
    @Test
    public void testDeposit() {
        Person person = new Person();
        person.setName("Henry");
        person.setLastname("Adams");
        person.setDateOfBirth(LocalDate.of(1990, 4, 4));
        person.setPESEL("88888888888");
        person = personService.createPerson(person);
        Account account = accountService.createAccount(person);
        account = accountService.deposit(account.getId(), new BigDecimal("1000.00"));
        assertEquals(new BigDecimal("1000.00"), account.getBalance(), "Balance should be 1000.00 after deposit");
    }

    // Test 8: Withdraw funds from an account.
    @Test
    public void testWithdraw() {
        Person person = new Person();
        person.setName("Ian");
        person.setLastname("Kelly");
        person.setDateOfBirth(LocalDate.of(1995, 5, 5));
        person.setPESEL("99999999999");
        person = personService.createPerson(person);
        Account account = accountService.createAccount(person);
        account = accountService.deposit(account.getId(), new BigDecimal("500.00"));
        account = accountService.withdraw(account.getId(), new BigDecimal("200.00"));
        assertEquals(new BigDecimal("300.00"), account.getBalance(), "Balance should be 300.00 after withdrawal");
    }

    // Test 10: Change account owner.
    @Test
    public void testChangeAccountOwner() {
        Person person1 = new Person();
        person1.setName("Karen");
        person1.setLastname("Miller");
        person1.setDateOfBirth(LocalDate.of(1988, 3, 3));
        person1.setPESEL("12121212121");
        person1 = personService.createPerson(person1);
        Person person2 = new Person();
        person2.setName("Laura");
        person2.setLastname("Davis");
        person2.setDateOfBirth(LocalDate.of(1990, 7, 7));
        person2.setPESEL("13131313131");
        person2 = personService.createPerson(person2);
        Account account = accountService.createAccount(person1);
        Account updatedAccount = accountService.changeAccountOwner(account.getId(), person2);
        assertEquals("Laura", updatedAccount.getOwner().getName(), "Account owner should change to Laura");
    }

    // Test 11: Retrieve account by ID.
    @Test
    public void testGetAccountById() {
        Person person = new Person();
        person.setName("Mark");
        person.setLastname("Wilson");
        person.setDateOfBirth(LocalDate.of(1982, 8, 8));
        person.setPESEL("14141414141");
        person = personService.createPerson(person);
        Account account = accountService.createAccount(person);
        Account found = accountService.getAccountById(account.getId());
        assertEquals(account.getId(), found.getId(), "Retrieved account ID should match");
    }

    // Test 12: Retrieve all accounts.
    @Test
    public void testGetAllAccounts() {
        int initialCount = accountService.getAllAccounts().size();
        Person person = new Person();
        person.setName("Nina");
        person.setLastname("Robinson");
        person.setDateOfBirth(LocalDate.of(1990, 9, 9));
        person.setPESEL("15151515151");
        person = personService.createPerson(person);
        accountService.createAccount(person);
        accountService.createAccount(person);
        List<Account> accounts = accountService.getAllAccounts();
        assertEquals(initialCount + 2, accounts.size(), "There should be two more accounts");
    }

    // Test 13: Delete an account.
    @Test
    public void testDeleteAccount() {
        Person person = new Person();
        person.setName("Oscar");
        person.setLastname("Martinez");
        person.setDateOfBirth(LocalDate.of(1995, 10, 10));
        person.setPESEL("16161616161");
        person = personService.createPerson(person);
        Account account = accountService.createAccount(person);
        int id = account.getId();
        accountService.deleteAccountById(id);
        Exception exception = assertThrows(RuntimeException.class, () -> accountService.getAccountById(id));
        String expectedMessage = "Account with ID";
        assertTrue(exception.getMessage().contains(expectedMessage), "Exception should indicate account not found");
    }

    // Test 14: Multiple deposits.
    @Test
    public void testMultipleDeposits() {
        Person person = new Person();
        person.setName("Paul");
        person.setLastname("Harris");
        person.setDateOfBirth(LocalDate.of(1985, 11, 11));
        person.setPESEL("17171717171");
        person = personService.createPerson(person);
        Account account = accountService.createAccount(person);
        account = accountService.deposit(account.getId(), new BigDecimal("200.00"));
        account = accountService.deposit(account.getId(), new BigDecimal("300.00"));
        account = accountService.deposit(account.getId(), new BigDecimal("500.00"));
        assertEquals(new BigDecimal("1000.00"), account.getBalance(), "Final balance should be 1000.00");
    }

    // Test 15: Deposit then withdraw series.
    @Test
    public void testDepositThenWithdrawSeries() {
        Person person = new Person();
        person.setName("Quinn");
        person.setLastname("Walker");
        person.setDateOfBirth(LocalDate.of(1990, 12, 12));
        person.setPESEL("18181818181");
        person = personService.createPerson(person);
        Account account = accountService.createAccount(person);
        account = accountService.deposit(account.getId(), new BigDecimal("1000.00"));
        account = accountService.withdraw(account.getId(), new BigDecimal("400.00"));
        account = accountService.withdraw(account.getId(), new BigDecimal("100.00"));
        assertEquals(new BigDecimal("500.00"), account.getBalance(), "Final balance should be 500.00");
    }

    // Test 16: Creating an account with an unsaved person should throw an exception.
    @Test
    public void testCreateAccountWithUnsavedPerson() {
        Person unsaved = new Person();
        unsaved.setName("Rita");
        unsaved.setLastname("Lewis");
        unsaved.setDateOfBirth(LocalDate.of(1992, 2, 2));
        unsaved.setPESEL("19191919191");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> accountService.createAccount(unsaved));
        String expectedMessage = "Owner must be saved before creating an account";
        assertTrue(exception.getMessage().contains(expectedMessage), "Exception should indicate unsaved owner");
    }

    // Test 17: Updating an account changes the owner.
    @Test
    public void testUpdateAccountChangesOwner() {
        Person person1 = new Person();
        person1.setName("Sam");
        person1.setLastname("Adams");
        person1.setDateOfBirth(LocalDate.of(1988, 3, 3));
        person1.setPESEL("20202020202");
        person1 = personService.createPerson(person1);
        Person person2 = new Person();
        person2.setName("Tina");
        person2.setLastname("Turner");
        person2.setDateOfBirth(LocalDate.of(1990, 4, 4));
        person2.setPESEL("21212121212");
        person2 = personService.createPerson(person2);
        Account account = accountService.createAccount(person1);
        Account updatedAccount = accountService.changeAccountOwner(account.getId(), person2);
        assertEquals("Tina", updatedAccount.getOwner().getName(), "Owner should be updated to Tina");
    }

    // Test 18: Simulate a deposit transaction strategy.
    @Test
    public void testDepositTransactionStrategy() {
        Person person = new Person();
        person.setName("Uma");
        person.setLastname("Nelson");
        person.setDateOfBirth(LocalDate.of(1995, 5, 5));
        person.setPESEL("22222222222");
        person = personService.createPerson(person);
        Account account = accountService.createAccount(person);
        account = accountService.deposit(account.getId(), new BigDecimal("300.00"));
        assertEquals(new BigDecimal("300.00"), account.getBalance(), "Balance should be 300.00 after deposit transaction");
    }

    // Test 19: Simulate a withdrawal transaction strategy.
    @Test
    public void testWithdrawalTransactionStrategy() {
        Person person = new Person();
        person.setName("Victor");
        person.setLastname("Scott");
        person.setDateOfBirth(LocalDate.of(1993, 6, 6));
        person.setPESEL("23232323232");
        person = personService.createPerson(person);
        Account account = accountService.createAccount(person);
        account = accountService.deposit(account.getId(), new BigDecimal("500.00"));
        account = accountService.withdraw(account.getId(), new BigDecimal("200.00"));
        assertEquals(new BigDecimal("300.00"), account.getBalance(), "Balance should be 300.00 after withdrawal transaction");
    }

    // Test 20: Simulate a transfer transaction between two accounts.
    @Test
    public void testTransferTransactionStrategy() {
        Person person1 = new Person();
        person1.setName("Wendy");
        person1.setLastname("Young");
        person1.setDateOfBirth(LocalDate.of(1987, 7, 7));
        person1.setPESEL("24242424242");
        person1 = personService.createPerson(person1);
        Person person2 = new Person();
        person2.setName("Xavier");
        person2.setLastname("Zimmer");
        person2.setDateOfBirth(LocalDate.of(1989, 8, 8));
        person2.setPESEL("25252525252");
        person2 = personService.createPerson(person2);
        Account account1 = accountService.createAccount(person1);
        Account account2 = accountService.createAccount(person2);
        account1 = accountService.deposit(account1.getId(), new BigDecimal("1000.00"));
        account2 = accountService.deposit(account2.getId(), new BigDecimal("500.00"));
        // Simulate a transfer: subtract 200 from account1 and add 200 to account2
        account1 = accountService.withdraw(account1.getId(), new BigDecimal("200.00"));
        account2 = accountService.deposit(account2.getId(), new BigDecimal("200.00"));
        assertEquals(new BigDecimal("800.00"), account1.getBalance(), "Account1 balance should be 800.00 after transfer");
        assertEquals(new BigDecimal("700.00"), account2.getBalance(), "Account2 balance should be 700.00 after transfer");
    }
}
