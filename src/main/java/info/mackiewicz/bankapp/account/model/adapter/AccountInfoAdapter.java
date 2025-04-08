package info.mackiewicz.bankapp.account.model.adapter;

import org.springframework.lang.NonNull;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.model.interfaces.AccountInfo;
import lombok.RequiredArgsConstructor;

/**
 * Adapter class that wraps an Account object and exposes only the information
 * required by the AccountInfo interface.
 */
@RequiredArgsConstructor
public class AccountInfoAdapter implements AccountInfo {
    
    private final Account account;
    
    /**
         * Returns the formatted IBAN from the associated account.
         *
         * <p>This method delegates the call to the underlying account instance to obtain the IBAN
         * in its formatted form.</p>
         *
         * @return the formatted IBAN as a String
         */
    @Override
    public String getFormattedIban() {
        return account.getFormattedIban();
    }
    
    /**
     * Retrieves the full name of the account owner.
     *
     * @return the account owner's full name
     */
    @Override
    public String getOwnerFullname() {
        return account.getOwnerFullname();
    }
    
    /**
     * Creates an AccountInfo adapter from the specified Account instance.
     *
     * @param account the Account instance to wrap; must not be null.
     * @return a new AccountInfo adapter that exposes the account's information.
     * @throws NullPointerException if the account parameter is null.
     * @see AccountInfo
     * @see Account
     */
    public static AccountInfo fromAccount(@NonNull Account account) {
        return new AccountInfoAdapter(account);
    }
}