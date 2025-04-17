package info.mackiewicz.bankapp.system.shared;

import info.mackiewicz.bankapp.account.exception.AccountOwnershipException;
import info.mackiewicz.bankapp.user.exception.InvalidUserDataException;
import info.mackiewicz.bankapp.user.model.User;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AccountAuthorizationService {


    public boolean validateAccountOwnership(int accountId, @NonNull User owner) {
        if (accountId < 1) handleFaultyAuthorization(accountId, owner);
        if (owner.getAccounts() == null || owner.getAccounts().isEmpty()) throw new InvalidUserDataException("User accounts list is null");
        boolean isOwner = owner.getAccounts().stream()
                .anyMatch(account -> account.getId().equals(accountId));
        if (!isOwner) handleFaultyAuthorization(accountId, owner);
        return true;
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
