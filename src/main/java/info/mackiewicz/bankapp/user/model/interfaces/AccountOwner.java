package info.mackiewicz.bankapp.user.model.interfaces;

import info.mackiewicz.bankapp.core.account.model.Account;

import java.util.Set;

public interface AccountOwner {
    Set<Account> getAccounts();
    Integer getAccountCounter();
}
