package info.mackiewicz.bankapp.testutils;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.user.model.User;
import org.iban4j.Iban;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TestAccountBuilder {
    
    public static Account createTestAccount(Integer id, BigDecimal balance, User owner) {
        try {
            Account account = Account.factory().createAccount(owner);
            
            // Use reflection to set private fields
            setPrivateField(account, "id", id);
            setPrivateField(account, "balance", balance);
            setPrivateField(account, "creationDate", LocalDateTime.now());
            
            return account;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create test account", e);
        }
    }

    public static Account createBankAccount() {
        try {
            // Create a system user for bank account
            User systemUser = TestUserBuilder.createSystemUser();
            Account account = Account.factory().createAccount(systemUser);
            
            // Use reflection to set private fields
            setPrivateField(account, "id", -1);
            setPrivateField(account, "balance", new BigDecimal("1000000.00"));
            setPrivateField(account, "creationDate", LocalDateTime.now());
            
            // Set the predefined bank IBAN
            Iban bankIban = Iban.valueOf("PL66485112340000000000000000");
            setPrivateField(account, "iban", bankIban);
            
            return account;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create bank account", e);
        }
    }

    private static void setPrivateField(Object object, String fieldName, Object value) {
        try {
            Field field = Account.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set private field: " + fieldName, e);
        }
    }
}