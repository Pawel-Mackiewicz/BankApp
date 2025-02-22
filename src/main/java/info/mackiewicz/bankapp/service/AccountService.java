package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.exception.AccountNotFoundByIdException;
import info.mackiewicz.bankapp.exception.OwnerAccountsNotFoundException;
import info.mackiewicz.bankapp.model.Account;
import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.repository.AccountRepository;
import info.mackiewicz.bankapp.utils.IbanGenerator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
    private final AccountRepository accountRepository;
    private final UserService userService;

    public AccountService(AccountRepository accountRepository, UserService userService) {
        this.accountRepository = accountRepository;
        this.userService = userService;
    }

    @Transactional
    public Account createAccount(Integer userId) {
        logger.info("createAccount: Starting with userId: {}", userId);
        User user = userService.getUserById(userId);
        logger.info("createAccount: userService.getUserById returned: {}", user);
        Account account = new Account(user);
        logger.info("createAccount: Account created: {}", account);
        account = setupIban(account);
        logger.info("createAccount: setupIban returned: {}", account);

        logger.info("createAccount: Calling accountRepository.save with account: {}", account);
        Account savedAccount = accountRepository.save(account);
        logger.info("createAccount: accountRepository.save returned: {}", savedAccount);
        logger.info("createAccount: Ending");
        return savedAccount;
    }

    public Account setupIban(Account account) {
        String iban = IbanGenerator.generateIban(account.getOwner().getId(), account.getUserAccountNumber());
        account.setIban(iban);
        return account;
    }

    public Account getAccountById(int id) {
        logger.info("getAccountById: Starting with id: {}", id);
        Optional<Account> account = accountRepository.findById(id);
        logger.info("getAccountById: accountRepository.findById returned: {}", account);
        Account result = account.orElseThrow(() -> new AccountNotFoundByIdException("Account with ID " + id + " not found."));
        logger.info("getAccountById: Ending with result: {}", result);
        return result;
    }

    public List<Account> getAccountsByOwnersPESEL(String pesel) {
        return accountRepository.findAccountsByOwner_PESEL(pesel)
                .orElseThrow(() -> new OwnerAccountsNotFoundException(
                        "User with PESEL " + pesel + " does not have any account."));
    }

    public List<Account> getAccountsByOwnersUsername(String username) {
        return accountRepository.findAccountsByOwner_username(username)
                .orElseThrow(
                        () -> new OwnerAccountsNotFoundException("User: " + username + " does not have any account."));
    }

    public List<Account> getAccountsByOwnersId(Integer id) {
        return accountRepository.findAccountsByOwner_id(id)
                .orElseThrow(
                        () -> new OwnerAccountsNotFoundException("User with ID " + id + " does not have any account."));
    }

    public Optional<Account> findByIban(String iban) {
        return accountRepository.findByIban(iban);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public void deleteAccountById(int id) {
        logger.info("deleteAccountById: Starting with id: {}", id);
        Account account = getAccountById(id);
        logger.info("deleteAccountById: getAccountById returned: {}", account);
        accountRepository.delete(account);
        logger.info("deleteAccountById: accountRepository.delete called");
        logger.info("deleteAccountById: Ending");
    }

    public Account changeAccountOwner(int id, int newId) {
        Account account = getAccountById(id);
        account.setOwner(userService.getUserById(newId));
        return accountRepository.save(account);
    }

    // FINANCIAL OPERATIONS

    @Transactional
    public Account deposit(int accountId, BigDecimal amount) {
        logger.info("deposit: Starting with accountId: {}, amount: {}", accountId, amount);
        Account account = getAccountById(accountId);
        logger.info("deposit: getAccountById returned: {}", account);
        account.deposit(amount);
        logger.info("deposit: Account balance after deposit: {}", account.getBalance());
        logger.info("deposit: Calling accountRepository.save with account: {}", account);
        Account savedAccount = accountRepository.save(account);
        logger.info("deposit: accountRepository.save returned: {}", savedAccount);
        logger.info("deposit: Ending");
        return savedAccount;
    }

    @Transactional
    public Account withdraw(int accountId, BigDecimal amount) {
        logger.info("withdraw: Starting with accountId: {}, amount: {}", accountId, amount);
        Account account = getAccountById(accountId);
        logger.info("withdraw: getAccountById returned: {}", account);
        account.withdraw(amount);
        logger.info("withdraw: Account balance after withdraw: {}", account.getBalance());
        logger.info("withdraw: Calling accountRepository.save with account: {}", account);
        Account savedAccount = accountRepository.save(account);
        logger.info("withdraw: accountRepository.save returned: {}", savedAccount);
        logger.info("withdraw: Ending");
        return savedAccount;
    }
}
