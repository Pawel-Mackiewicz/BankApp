package info.mackiewicz.bankapp.account.model;

import org.iban4j.Iban;

import info.mackiewicz.bankapp.account.util.IbanGenerator;
import info.mackiewicz.bankapp.user.model.User;
import lombok.extern.slf4j.Slf4j;

/**
 * Factory responsible for creating Account objects.
 * <p>
 * This class encapsulates the logic for creating bank accounts.
 * </p>
 */
@Slf4j
public class AccountFactory {
    
    /**
     * Creates a standard bank account for the specified user.
     * <p>
     * This method generates a unique account number and IBAN,
     * and creates a new Account instance.
     * </p>
     *
     * @param owner The user who will own the account
     * @return A newly created Account instance
     */
    public Account createAccount(User owner) {
        log.debug("Starting account creation for user: {}", owner.getId());
        Integer userAccountNumber = owner.getNextAccountNumber();
        log.debug("Got account number: {} for user: {}", userAccountNumber, owner.getId());
        
        Iban iban = IbanGenerator.generateIban(owner.getId(), userAccountNumber);
        log.debug("Generated IBAN: {} for accountNumber: {}", iban, userAccountNumber);
        
        return new Account(owner, userAccountNumber, iban);
    }
}