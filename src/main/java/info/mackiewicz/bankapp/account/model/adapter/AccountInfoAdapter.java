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
     * Retrieves the formatted IBAN from the associated account.
     *
     * <p>This method delegates to the underlying account's getFormattedIban method to obtain the formatted IBAN string.</p>
     *
     * @return the account's formatted IBAN
     */
    @Override
    public String getFormattedIban() {
        return account.getFormattedIban();
    }
    
    /**
     * Retrieves the full name of the account owner.
     *
     * <p>This method delegates the call to the underlying Account object to obtain the owner's full name.</p>
     *
     * @return the full name of the account owner.
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