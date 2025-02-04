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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AccountServiceOperationsTest {

    @Autowired
    private PersonService personService;

    @Autowired
    private AccountService accountService;

    @Test
    public void testCreateAccountAndOperations() {
        // Arrange: Create and persist a Person
        Person person = new Person();
        person.setName("John");
        person.setLastname("Doe");
        person.setDateOfBirth(LocalDate.of(1980, 1, 1));
        person.setPESEL("98765432100");
        person = personService.createPerson(person);
        assertNotNull(person.getId(), "The person must have an ID after being saved.");

        // Act: Create an Account for the Person
        Account account = accountService.createAccount(person);
        assertNotNull(account, "Account should not be null after creation.");
        assertNotNull(account.getId(), "Account should have an assigned ID.");
        // Initial balance is expected to be zero.
        assertEquals(BigDecimal.ZERO, account.getBalance(), "Initial account balance should be zero.");

        // Act: Deposit funds into the account
        account = accountService.deposit(account.getId(), new BigDecimal("1000.00"));
        assertEquals(new BigDecimal("1000.00"), account.getBalance(), "Account balance should be 1000.00 after deposit.");

        // Act: Withdraw funds from the account
        account = accountService.withdraw(account.getId(), new BigDecimal("250.00"));
        // 1000.00 - 250.00 = 750.00
        assertEquals(new BigDecimal("750.00"), account.getBalance(), "Account balance should be 750.00 after withdrawal.");
    }
}
