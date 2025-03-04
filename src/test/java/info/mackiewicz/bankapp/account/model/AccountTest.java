package info.mackiewicz.bankapp.account.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.iban4j.Iban;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import info.mackiewicz.bankapp.account.model.dto.AccountOwnerDTO;
import info.mackiewicz.bankapp.user.model.User;

class AccountTest {

    private Account account1;
    private Account account2;
    private User owner;
    private final String IBAN1 = "PL52485112340000170000000001";
    private final String IBAN2 = "PL65485112340000340000000001";

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1);
        owner.setFirstname("Jan");
        owner.setLastname("Kowalski");
        
        account1 = TestAccountBuilder.createTestAccountWithOwner(owner);
        TestAccountBuilder.setField(account1, "iban", Iban.valueOf(IBAN1));
        TestAccountBuilder.setField(account1, "id", 1);
        TestAccountBuilder.setField(account1, "userAccountNumber", 1001);
        TestAccountBuilder.setField(account1, "balance", new BigDecimal("1000.00"));
        TestAccountBuilder.setField(account1, "creationDate", LocalDateTime.now());

        account2 = TestAccountBuilder.createTestAccountWithOwner(owner);
        TestAccountBuilder.setField(account2, "iban", Iban.valueOf(IBAN2));
        TestAccountBuilder.setField(account2, "id", 2);
        TestAccountBuilder.setField(account2, "userAccountNumber", 1002);
        TestAccountBuilder.setField(account2, "balance", new BigDecimal("2000.00"));
        TestAccountBuilder.setField(account2, "creationDate", LocalDateTime.now());
    }

    @Test
    void getFormattedIban_ShouldReturnFormattedIbanString() {
        // when
        String formattedIban = account1.getFormattedIban();

        // then
        assertTrue(formattedIban.contains(" "));
        assertEquals(IBAN1.length() + 6, formattedIban.length()); // 6 spaces in formatted IBAN
        assertTrue(formattedIban.replace(" ", "").equals(IBAN1));
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
    void equals_WithSameIban_ShouldReturnTrue() {
        // given
        Account sameIbanAccount = TestAccountBuilder.createTestAccountWithOwner(owner);
        TestAccountBuilder.setField(sameIbanAccount, "iban", account1.getIban());

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
        assertEquals(Iban.valueOf(IBAN1), account1.getIban());
        assertEquals(1001, account1.getUserAccountNumber());
        assertEquals(new BigDecimal("1000.00"), account1.getBalance());
        assertNotNull(account1.getCreationDate());
    }
}