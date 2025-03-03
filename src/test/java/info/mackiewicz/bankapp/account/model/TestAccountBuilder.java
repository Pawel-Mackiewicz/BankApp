package info.mackiewicz.bankapp.account.model;

import info.mackiewicz.bankapp.user.model.User;
import org.iban4j.Iban;
import java.math.BigDecimal;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * Test utility class for building Account instances in test cases
 */
public class TestAccountBuilder {
    
    // Valid IBAN for testing purposes
    private static final String TEST_IBAN = "PL34109010140000071219812874";
    
    public static Account createTestAccount() {
        return createTestAccountWithBalance(BigDecimal.ZERO);
    }
    
    public static Account createTestAccountWithBalance(BigDecimal balance) {
        try {
            Account account = new Account();
            setField(account, "balance", balance);
            setField(account, "iban", Iban.valueOf(TEST_IBAN));
            return account;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create test account", e);
        }
    }
    
    public static Account createTestAccountWithId(Integer id) {
        Account account = createTestAccount();
        setField(account, "id", id);
        return account;
    }
    
    public static Account createTestAccountWithOwner(User owner) {
        Account account = createTestAccount();
        setField(account, "owner", owner);
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

    /**
     * Returns the valid test IBAN string
     */
    public static String getTestIban() {
        return TEST_IBAN;
    }
}
