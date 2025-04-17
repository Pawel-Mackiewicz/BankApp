package info.mackiewicz.bankapp.account.model.adapter;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.model.interfaces.AccountInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

/**
 * Adapter class that wraps an Account object and exposes only the information
 * required by the AccountInfo interface.
 */
@RequiredArgsConstructor
public class AccountInfoAdapter implements AccountInfo {

    private final Account account;

    /**
     * Creates an AccountInfo adapter from an Account object.
     *
     * @return an AccountInfo adapter.
     * @throws NullPointerException if the account is null.
     * @see AccountInfo
     * @see Account
     */
    public static AccountInfo fromAccount(@NonNull Account account) {
        return new AccountInfoAdapter(account);
    }

    @Override
    public Integer getId() {
        return account.getId();
    }

    @Override
    public String getFormattedIban() {
        return account.getFormattedIban();
    }

    @Override
    public String getOwnerFullname() {
        return account.getOwnerFullname();
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof AccountInfoAdapter that)) return false;

        return account.equals(that.account);
    }

    @Override
    public int hashCode() {
        return account.hashCode();
    }
}