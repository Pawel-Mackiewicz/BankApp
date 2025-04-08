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
     * Creates a new Account instance with a balance of zero.
     *
     * <p>This utility method returns a test account by delegating to 
     * {@code createTestAccountWithBalance} with a balance of {@code BigDecimal.ZERO}.</p>
     *
     * @return a new Account with an initial zero balance
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
     * Creates a test Account for the specified owner with a unique IBAN and a default balance.
     *
     * <p>This method initializes a new account by setting a generated IBAN, assigning the provided owner,
     * and setting the balance to a predefined constant. It also ensures that the owner's account collection
     * is initialized before adding the new account.</p>
     *
     * @param owner the User to be set as the owner of the account
     * @return the newly created Account associated with the specified owner
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
     * Creates a test Account instance with a randomly generated owner.
     *
     * <p>This method generates a random test user using {@code TestUserBuilder.createRandomTestUser()}, creates an Account
     * instance via the account factory, and sets its ID and balance using reflection. The account ID is set to a predefined
     * random integer constant, and the balance is set to a default test value.
     * </p>
     *
     * @return a new Account instance with a random owner, preset account ID, and default balance.
     */
    public static Account createTestAccountWithRandomOwner() {
        User owner = TestUserBuilder.createRandomTestUser();
        Account account = Account.factory().createAccount(owner);
        setField(account, "id", RANDOM_INT);
        setField(account, "balance", BALANCE);
        return account;
    }

    /**
     * Creates a test Account instance with a fixed test owner.
     *
     * <p>This method generates a test user using TestUserBuilder and creates an Account for that user. It then assigns a predetermined ID (RANDOM_INT) and initializes the account balance to the default value (BALANCE).
     *
     * @return the newly created Account instance with a test user as its owner
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
