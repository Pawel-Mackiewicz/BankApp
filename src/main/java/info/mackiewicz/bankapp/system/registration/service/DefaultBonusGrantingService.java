package info.mackiewicz.bankapp.system.registration.service;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.shared.util.BankAccountProvider;
import info.mackiewicz.bankapp.system.transaction.processing.TransactionProcessingService;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iban4j.Iban;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Default implementation of the BonusGrantingService for managing welcome bonuses for newly registered users.
 * <p>
 * This service facilitates the transfer of funds from a default bank account to the recipient's account
 * as a welcome bonus. It handles the entire process from retrieving account information to processing
 * the transaction.
 * <p>
 * The service uses a predefined bank account (identified by a static IBAN) as the source for all bonus
 * transfers, and applies a standard title to all welcome bonus transactions.
 * <p>
 * Thread safety: This class is thread-safe as it maintains no mutable state and relies on thread-safe
 * services for all operations.
 *
 * @see BonusGrantingService
 * @see info.mackiewicz.bankapp.account.service.AccountService
 * @see info.mackiewicz.bankapp.transaction.service.TransactionService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultBonusGrantingService implements BonusGrantingService {

    public static final String DEFAULT_TITLE = "Welcome bonus";

    private final BankAccountProvider bankAccountProvider;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final TransactionProcessingService transactionProcessingService;


    //TODO: Write down all exceptions that can be thrown.
    @Override
    public void grantWelcomeBonus(@NonNull Iban recipientIban, @NonNull BigDecimal amount) {

        Transaction transaction = buildWelcomeBonusTransaction(recipientIban, amount);
        log.trace("Built welcome bonus transaction");
        Transaction registeredTransaction = transactionService.registerTransaction(transaction);
        log.trace("Registered welcome bonus transaction");
        transactionProcessingService.processTransactionById(registeredTransaction.getId());
        log.trace("Processed welcome bonus transaction");

    }

    //Transactions are build from Ibans for future developement.
    // So we don't have to rework the public `grantWelcomeBonus` method.
    private Transaction buildWelcomeBonusTransaction(Iban recipientIban, BigDecimal amount) {
        Account bank = bankAccountProvider.getBankAccount();
        log.trace("Gathered bank account");
        Account recipient = accountService.getAccountByIban(recipientIban);
        log.trace("Gathered recipient account");

        return Transaction.buildTransfer()
                .from(bank)
                .to(recipient)
                .withAmount(amount)
                .withTitle(DEFAULT_TITLE)
                .build();
    }
}
