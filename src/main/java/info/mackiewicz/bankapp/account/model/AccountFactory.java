package info.mackiewicz.bankapp.account.model;

import org.iban4j.Iban;
import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.account.util.IbanGenerator;
import info.mackiewicz.bankapp.shared.util.RetryUtil;
import info.mackiewicz.bankapp.user.model.User;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * Factory responsible for creating Account objects.
 * <p>
 * This class encapsulates the logic for creating bank accounts.
 * </p>
 */
@Slf4j
@Component
public class AccountFactory {
    
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 100;
    
    /**
     * Creates a standard bank account for the specified user.
     * <p>
     * This method validates the owner, generates a unique account number and IBAN,
     * and creates a new Account instance.
     * </p>
     *
     * @param owner The user who will own the account
     * @return A newly created Account instance
     * @throws IllegalArgumentException if the user cannot own a new account
     */
    public Account createAccount(User owner) {
        log.debug("Starting account creation for user: {}", owner.getId());
        Integer userAccountNumber = owner.getNextAccountNumber();
        log.debug("Got account number: {} for user: {}", userAccountNumber, owner.getId());
        
        Iban iban = IbanGenerator.generateIban(owner.getId(), userAccountNumber);
        log.debug("Generated IBAN: {} for accountNumber: {}", iban, userAccountNumber);
        
        return new Account(owner, userAccountNumber, iban);
    }
    
    /**
     * Creates an account with retry logic.
     * <p>
     * This method attempts to create an account using the provided user supplier,
     * and will retry if it fails, up to a maximum number of retries with
     * increasing delays between attempts.
     * </p>
     *
     * @param userSupplier A supplier function that provides the user for account creation
     * @param userId The ID of the user (used for logging purposes)
     * @return The newly created account
     * @throws RuntimeException if account creation fails after all retry attempts
     */
    public Account createAccountWithRetry(Supplier<User> userSupplier, Integer userId) {
        log.debug("Creating account with retry for user ID: {}", userId);
        return RetryUtil.executeWithRetry(
            () -> {
                User user = userSupplier.get();
                return createAccount(user);
            },
            MAX_RETRIES,
            RETRY_DELAY_MS,
            "create account",
            "userId: " + userId
        );
    }
}