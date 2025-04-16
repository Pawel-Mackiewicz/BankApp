package info.mackiewicz.bankapp.system.banking.operations.service.helpers;

import info.mackiewicz.bankapp.account.exception.AccountNotFoundByIbanException;
import info.mackiewicz.bankapp.account.exception.AccountOwnershipException;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iban4j.Iban;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountSecurityService {

    private final AccountService accountService;

    /**
     * Validates the ownership of an account by a user.
     *
     * @param userId      The ID of the user
     * @param accountIban The IBAN of the account to validate
     * @return The account if the user is the owner
     * @throws AccountOwnershipException if the user does not own the account
     * @throws AccountNotFoundByIbanException if the account is not found
     */
    public Account retrieveValidatedAccount(Integer userId, Iban accountIban) {
        Account account = accountService.getAccountByIban(accountIban);
        Integer ownerId = account.getOwner().getId();

        if (!ownerId.equals(userId)) {
            log.warn("Unauthorized access attempt by user ID: {}", userId);
            throw new AccountOwnershipException("Unauthorized access to account");
        }
        return account;
    }
}
