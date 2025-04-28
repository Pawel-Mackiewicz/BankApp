package info.mackiewicz.bankapp.core.account.model;

import info.mackiewicz.bankapp.testutils.TestIbanProvider;
import info.mackiewicz.bankapp.testutils.TestUserBuilder;
import info.mackiewicz.bankapp.user.model.User;
import org.iban4j.Iban;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Random;

/**
 * Test utility class for building Account instances in test cases
 */
public class TestAccountBuilder {

    private static final int RANDOM_INT = new Random().nextInt(20);
    private static final BigDecimal BALANCE = BigDecimal.valueOf(1000.00);

    public static Account createTestAccount() {
        return createTestAccountWithBalance(BigDecimal.ZERO);
    }

    public static Account createTestAccountWithBalance(BigDecimal balance) {
        try {
            Account account = new Account();
            setField(account, "balance", balance);
            setField(account, "id", RANDOM_INT);
            setField(account, "iban", Iban.valueOf(getNextIban()));
            return account;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create test account", e);
        }
    }

    public static Account createTestAccountWithOwner(User owner) {
        Account account = createTestAccountWithBalance(BALANCE);
        setField(account, "owner", owner);
        if (owner.getAccounts() == null) {
            owner.setAccounts(new HashSet<>());
        }
        owner.getAccounts().add(account);
        return account;
    }

    public static Account createTestAccountWithRandomOwner() {
        User owner = TestUserBuilder.createRandomTestUser();
        Account account = Account.factory().createAccount(owner);
        setField(account, "id", RANDOM_INT);
        setField(account, "balance", BALANCE);
        return account;
    }

    public static Account createTestAccountWithSameOwner() {
        User owner = TestUserBuilder.createTestUser();
        Account account = Account.factory().createAccount(owner);
        setField(account, "id", RANDOM_INT);
        setField(account, "balance", BALANCE);
        return account;
    }

    public static void setField(Account account, String fieldName, Object value) {
        try {
            Field field = Account.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(account, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field " + fieldName, e);
        }
    }

    private static String getNextIban() {
        return TestIbanProvider.getNextIban();
    }
}
