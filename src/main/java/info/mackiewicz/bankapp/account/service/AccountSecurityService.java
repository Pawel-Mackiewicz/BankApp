package info.mackiewicz.bankapp.account.service;

import org.iban4j.Iban;
import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.account.exception.AccountOwnershipException;
import info.mackiewicz.bankapp.account.model.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    public Account validateAccountOwnership(Integer userId, Iban accountIban) {
        log.debug("Validating account ownership for user ID: {} and account Iban: {}", userId, accountIban);
        Account account = accountService.getAccountByIban(accountIban);
        Integer ownerId = account.getOwner().getId();

        if (!ownerId.equals(userId)) {
            log.warn("Unauthorized access attempt by user ID: {}", userId);
            throw new AccountOwnershipException("Unauthorized access to account");
        }
        log.debug("Account ownership validated for user ID: {} and account Iban: {}", userId, accountIban);
        return account;
    }
}
