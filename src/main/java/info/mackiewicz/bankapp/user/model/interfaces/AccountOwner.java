package info.mackiewicz.bankapp.user.model.interfaces;

import java.util.Set;

import info.mackiewicz.bankapp.account.model.Account;

public interface AccountOwner {
    Set<Account> getAccounts();
    Integer getAccountCounter();
}
