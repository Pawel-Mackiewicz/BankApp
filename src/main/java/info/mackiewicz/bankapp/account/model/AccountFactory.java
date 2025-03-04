package info.mackiewicz.bankapp.account.model;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.account.service.AccountValidationService;
import info.mackiewicz.bankapp.account.util.IbanGenerator;
import info.mackiewicz.bankapp.user.model.User;
import lombok.RequiredArgsConstructor;

/**
 * Factory responsible for creating Account objects.
 * <p>
 * This class encapsulates the logic for creating different types of bank accounts
 * and ensures all necessary validations are performed during account creation.
 * </p>
 */
@RequiredArgsConstructor
@Component
public class AccountFactory {

    private final AccountValidationService validationService;
    
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
        if (owner == null) {
            throw new NullPointerException("Owner cannot be null");
        }
        validationService.validateNewAccountOwner(owner);
        Integer userAccountNumber = owner.getNextAccountNumber();
        String iban = IbanGenerator.generateIban(owner.getId(), userAccountNumber);
        
        return new Account(owner, userAccountNumber, iban);
    }
}