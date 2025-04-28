package info.mackiewicz.bankapp.core.account.service;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.account.repository.AccountRepository;
import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.core.user.service.UserService;
import info.mackiewicz.bankapp.shared.util.RetryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service responsible for orchestrating the account creation process.
 * <p>
 * This service handles the retry logic and user retrieval for account creation,
 * working with AccountFactory to create accounts and AccountRepository to save them.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
class AccountCreationService {

    private final UserService userService;
    private final AccountRepository accountRepository;
    private final AccountValidationService validationService;
    

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 100;

    /**
     * Creates a new account for the specified user with retry logic.
     * <p>
     * This method validates the user, retrieves the user with a pessimistic lock,
     * creates an account using AccountFactory, and saves it to the repository.
     * If any step fails, it will retry up to a maximum number of retries.
     * </p>
     *
     * @param userId The ID of the user who will own the account
     * @return The newly created and saved account
     * @throws RuntimeException if account creation fails after all retry attempts
     */
    Account createAccount(Integer userId) {
        log.debug("Starting account creation process for user ID: {}", userId);
        
        // Validate user before attempting to create account
        User owner = userService.getUserById(userId);
        validationService.validateNewAccountOwner(owner);
        
        // Execute account creation with retry logic
        return executeWithRetry(userId);
    }

    /**
     * Executes the account creation process with retry logic.
     *
     * @param userId The ID of the user who will own the account
     * @return The newly created and saved account
     */
    private Account executeWithRetry(Integer userId) {
        return RetryUtil.executeWithRetry(
            () -> createAndSaveAccount(userId),
            MAX_RETRIES,
            RETRY_DELAY_MS,
            "create account",
            "userId: " + userId
        );
    }

    /**
     * Creates and saves an account for the specified user.
     * <p>
     * This method retrieves the user with a pessimistic lock,
     * creates an account using AccountFactory, and saves it to the repository.
     * </p>
     *
     * @param userId The ID of the user who will own the account
     * @return The newly created and saved account
     */
    private Account createAndSaveAccount(Integer userId) {
        // Get user with pessimistic lock to prevent concurrent modifications
        User user = userService.getUserByIdWithPessimisticLock(userId);
        
        // Create account using factory
        Account account = Account.factory().createAccount(user);
        
        // Save account to repository
        Account savedAccount = accountRepository.save(account);
        log.debug("Account created successfully with ID: {}", savedAccount.getId());
        
        return savedAccount;
    }
}