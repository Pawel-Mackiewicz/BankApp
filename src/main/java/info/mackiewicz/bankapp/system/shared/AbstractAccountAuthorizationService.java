package info.mackiewicz.bankapp.system.shared;

import info.mackiewicz.bankapp.core.account.exception.AccountOwnershipException;
import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.user.exception.InvalidUserDataException;
import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.core.user.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
@Scope("prototype")
public abstract class AbstractAccountAuthorizationService<T> implements AccountAuthorizationService<T> {
    private boolean updated = false;

    private final UserService userService;

    @Override
    public void validateAccountOwnership(@NonNull T accountIdentifier, @NonNull User owner) {
        Set<Account> accounts = owner.getAccounts();
        if (accounts == null || accounts.isEmpty()) {
            throw new InvalidUserDataException("User accounts list is null or empty");
        }
        log.info("Validating account ownership. Updated: {}, Accounts visible: {}", updated, accounts.size());




        if (!isOwner(accountIdentifier, accounts)) {
            //this is for making sure that we have updated user data
            //problem is visible if in one session user will make new accounts
            // and then try to check their history.
            // These accounts won't be visible in #authentication.principal
            if (!updated) {
                User updatedOwner = userService.getUserById(owner.getId());
                updated = true;
                validateAccountOwnership(accountIdentifier, updatedOwner);
            } else {
                handleFaultyAuthorization(accountIdentifier.toString(), owner);
            }
        }
        updated = false;
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
