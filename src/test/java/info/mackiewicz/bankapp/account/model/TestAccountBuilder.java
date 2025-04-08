package info.mackiewicz.bankapp.account.model;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.iban4j.Iban;

import info.mackiewicz.bankapp.testutils.TestUserBuilder;
import info.mackiewicz.bankapp.user.model.User;

/**
 * Test utility class for building Account instances in test cases
 */
public class TestAccountBuilder {

    private static final List<String> TEST_IBANS = List.of(
            "PL66485112340000000000000000",
            "PL52485112340000170000000001",
            "PL65485112340000340000000001",
            "PL78485112340000510000000001");
    private static int currentIbanIndex = 0;
    private static final int RANDOM_INT = new Random().nextInt(20);
    private static final BigDecimal BALANCE = BigDecimal.valueOf(1000.00);

    /**
     * Creates a test Account with a zero balance.
     *
     * <p>This method delegates account creation to {@link #createTestAccountWithBalance(BigDecimal)} using a balance of {@link BigDecimal#ZERO}.</p>
     *
     * @return a new Account instance with a balance of zero
     */
    public static Account createTestAccount() {
        return createTestAccountWithBalance(BigDecimal.ZERO);
    }

    public static Account createTestAccountWithBalance(BigDecimal balance) {
        try {
            Account account = new Account();
            setField(account, "balance", balance);
            return account;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create test account", e);
        }
    }

    public static Account createTestAccountWithId(Integer id) {
        Account account = createTestAccount();
        setField(account, "id", id);
        String iban = getNextIban();
        setField(account, "iban", Iban.valueOf(iban));
        return account;
    }

    /**
     * Creates a test account for the specified owner.
     *
     * This method initializes a new account with a unique IBAN, assigns the provided owner, and
     * sets a default balance. If the owner's account set is not already initialized, it creates a new set.
     * The account is then added to the owner's account collection.
     *
     * @param owner the user who will own the created account
     * @return the newly created test account configured with the owner, IBAN, and default balance
     */
    public static Account createTestAccountWithOwner(User owner) {
        Account account = createTestAccount();
        String iban = getNextIban();
        setField(account, "iban", Iban.valueOf(iban));
        setField(account, "owner", owner);
        setField(account, "balance", BALANCE);
        if (owner.getAccounts() == null) {
            owner.setAccounts(new HashSet<>());
        }
        owner.getAccounts().add(account);
        return account;
    }

    /**
     * Creates a test account instance with a randomly generated owner.
     *
     * <p>This method generates a random test user and uses it as the owner for a new account.
     * The account's identifier is set to a random integer value, and its balance is initialized
     * to a default test balance.
     *
     * @return the newly created Account instance
     */
    public static Account createTestAccountWithRandomOwner() {
        User owner = TestUserBuilder.createRandomTestUser();
        Account account = Account.factory().createAccount(owner);
        setField(account, "id", RANDOM_INT);
        setField(account, "balance", BALANCE);
        return account;
    }

    /**
     * Creates a test account with a predefined test user as its owner.
     *
     * <p>This method generates a test user via {@code TestUserBuilder.createTestUser()}, creates an account using the Account factory,
     * assigns a random test ID, and sets the account's balance to a default value.
     *
     * @return the newly created Account instance configured with a test user owner, random ID, and default balance.
     */
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

    /**
     * Returns a valid test IBAN string
     */
    public static String getTestIban() {
        return getNextIban();
    }

    private static String getNextIban() {
        String iban = TEST_IBANS.get(currentIbanIndex);
        currentIbanIndex = (currentIbanIndex + 1) % TEST_IBANS.size();
        return iban;
    }
}
