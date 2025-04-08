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
     * Retrieves the formatted International Bank Account Number (IBAN) from the associated Account.
     *
     * @return the formatted IBAN string.
     */
    @Override
    public String getFormattedIban() {
        return account.getFormattedIban();
    }
    
    /**
     * Returns the account owner's full name.
     *
     * @return the full name of the account owner
     */
    @Override
    public String getOwnerFullname() {
        return account.getOwnerFullname();
    }
    
    /**
     * Creates an AccountInfo adapter from an Account object.
     * @return an AccountInfo adapter.
     * @throws NullPointerException if the account is null.
     * @see AccountInfo
     * @see Account
     */
    public static AccountInfo fromAccount(@NonNull Account account) {
        return new AccountInfoAdapter(account);
    }
}