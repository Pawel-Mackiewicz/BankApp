package info.mackiewicz.bankapp.account.model;

import info.mackiewicz.bankapp.account.model.dto.AccountOwnerDTO;
import info.mackiewicz.bankapp.testutils.TestIbanProvider;
import info.mackiewicz.bankapp.user.model.User;
import org.iban4j.Iban;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private Account account1;
    private Account account2;
    private User owner;
    private Iban testIban1;
    private Iban testIban2;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1);
        owner.setFirstname("Jan");
        owner.setLastname("Kowalski");

        // Initialize test IBANs
        testIban1 = TestIbanProvider.getIbanObject(0);
        testIban2 = TestIbanProvider.getIbanObject(1);
        
        account1 = TestAccountBuilder.createTestAccountWithOwner(owner);
        TestAccountBuilder.setField(account1, "iban", testIban1);
        TestAccountBuilder.setField(account1, "id", 1);
        TestAccountBuilder.setField(account1, "userAccountNumber", 1001);
        TestAccountBuilder.setField(account1, "balance", new BigDecimal("1000.00"));
        TestAccountBuilder.setField(account1, "creationDate", LocalDateTime.now());

        account2 = TestAccountBuilder.createTestAccountWithOwner(owner);
        TestAccountBuilder.setField(account2, "iban", testIban2);
        TestAccountBuilder.setField(account2, "id", 2);
        TestAccountBuilder.setField(account2, "userAccountNumber", 1002);
        TestAccountBuilder.setField(account2, "balance", new BigDecimal("2000.00"));
        TestAccountBuilder.setField(account2, "creationDate", LocalDateTime.now());
    }

    @Test
    void getFormattedIban_ShouldReturnFormattedIbanString() {
        // when
        String formattedIban = account1.getFormattedIban();
        String unformattedIban = testIban1.toString();

        // then
        assertTrue(formattedIban.contains(" "));
        assertEquals(unformattedIban.length() + 6, formattedIban.length()); // 6 spaces in formatted IBAN
        assertEquals(formattedIban.replace(" ", ""), unformattedIban);
    }

    @Test
    void getOwner_ShouldReturnCorrectAccountOwnerDTO() {
        // when
        AccountOwnerDTO ownerDTO = account1.getOwner();

        // then
        assertNotNull(ownerDTO);
        assertEquals(owner.getId(), ownerDTO.getId());
        assertEquals(owner.getFullName(), ownerDTO.getFullName());
    }

    @Test
    void toString_ShouldReturnCorrectFormat() {
        // when
        String accountString = account1.toString();
        String expectedFormat = String.format("Account IBAN #%s [balance = %.2f]", 
            account1.getIban().toFormattedString(), 
            account1.getBalance().doubleValue());

        // then
        assertEquals(expectedFormat, accountString);
    }

    @Test
    void equals_WithSameIbanAndBalance_ShouldReturnTrue() {
        // given
        Account sameIbanAccount = TestAccountBuilder.createTestAccountWithOwner(owner);
        TestAccountBuilder.setField(sameIbanAccount, "iban", account1.getIban());
        TestAccountBuilder.setField(sameIbanAccount, "balance", account1.getBalance());

        // when & then
        assertEquals(account1, sameIbanAccount);
    }

    @Test
    void equals_WithDifferentIban_ShouldReturnFalse() {
        // when & then
        assertNotEquals(account1, account2);
    }

    @Test
    void equals_WithNull_ShouldReturnFalse() {
        // when & then
        assertNotEquals(account1, null);
    }

    @Test
    void equals_WithDifferentClass_ShouldReturnFalse() {
        // when & then
        assertNotEquals(account1, new Object());
    }

    @Test
    void hashCode_WithSameIban_ShouldBeEqual() {
        // given
        Account sameIbanAccount = TestAccountBuilder.createTestAccountWithOwner(owner);
        TestAccountBuilder.setField(sameIbanAccount, "iban", account1.getIban());
        TestAccountBuilder.setField(sameIbanAccount, "id", account1.getId());

        // when & then
        assertEquals(account1.hashCode(), sameIbanAccount.hashCode());
    }

    @Test
    void hashCode_WithDifferentIban_ShouldNotBeEqual() {
        // when & then
        assertNotEquals(account1.hashCode(), account2.hashCode());
    }

    @Test
    void setBalance_WhenCalledDirectly_ShouldThrowSecurityException() {
        // when & then
        assertThrows(SecurityException.class, () -> {
            account1.setBalance(BigDecimal.TEN);
        });
    }

    @Test
    void getters_ShouldReturnCorrectValues() {
        // when & then
        assertEquals(1, account1.getId());
        assertEquals(testIban1, account1.getIban());
        assertEquals(1001, account1.getUserAccountNumber());
        assertEquals(new BigDecimal("1000.00"), account1.getBalance());
        assertNotNull(account1.getCreationDate());
    }
}