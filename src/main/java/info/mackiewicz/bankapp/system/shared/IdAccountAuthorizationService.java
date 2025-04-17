package info.mackiewicz.bankapp.system.shared;

import info.mackiewicz.bankapp.account.exception.AccountOwnershipException;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.user.exception.InvalidUserDataException;
import info.mackiewicz.bankapp.user.model.User;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
public class IdAccountAuthorizationService implements AccountAuthorizationService<Integer> {

    @Override
    public void validateAccountOwnership(@NonNull Integer accountId, @NonNull User owner) {
        if (accountId < 1) {
            handleFaultyAuthorization(accountId, owner);
        }

        Set<Account> accounts = owner.getAccounts();
        if (accounts == null || accounts.isEmpty()) {
            throw new InvalidUserDataException("User accounts list is null or empty");
        }

        if (!isOwner(accountId, accounts)) {
            handleFaultyAuthorization(accountId, owner);
        }
    }

    private static boolean isOwner(int accountId, Set<Account> accounts) {
        return accounts.stream()
                .anyMatch(account -> account.getId().equals(accountId));
    }

    private void handleFaultyAuthorization(int accountId, User owner) {
        String message = String.format(
                "User %s tried to access account with id %s without proper authorization.",
                owner.getId(),
                accountId);

        log.warn(message);
        throw new AccountOwnershipException(message);
    }
}
