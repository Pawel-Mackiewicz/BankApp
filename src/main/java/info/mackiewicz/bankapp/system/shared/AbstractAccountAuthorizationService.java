package info.mackiewicz.bankapp.system.shared;

import info.mackiewicz.bankapp.account.exception.AccountOwnershipException;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.user.exception.InvalidUserDataException;
import info.mackiewicz.bankapp.user.model.User;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public abstract class AbstractAccountAuthorizationService<T> implements AccountAuthorizationService<T> {

    @Override
    public void validateAccountOwnership(@NonNull T accountIdentifier, @NonNull User owner) {

        Set<Account> accounts = owner.getAccounts();
        if (accounts == null || accounts.isEmpty()) {
            throw new InvalidUserDataException("User accounts list is null or empty");
        }

        if (!isOwner(accountIdentifier, accounts)) {
            handleFaultyAuthorization(accountIdentifier.toString(), owner);
        }
    }

    protected abstract boolean isOwner(T accountIdentifier, Set<Account> accounts);

    private void handleFaultyAuthorization(String accountIdentifier, User owner) {
        String message = String.format(
                "User %s tried to access account with %s: %s without proper authorization.",
                owner.getId(),
                getIdentifierTypeName(),
                accountIdentifier);

        log.warn(message);
        throw new AccountOwnershipException(message);
    }
}
