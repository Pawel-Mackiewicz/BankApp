package info.mackiewicz.bankapp.system.registration.service;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
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

    private static final Iban DEFAULT_BANK_IBAN = Iban.valueOf("PL66485112340000000000000000");
    private static final String DEFAULT_TITLE = "Welcome bonus";

    private final AccountService accountService;
    private final TransactionService transactionService;


    /**
     * Grants a welcome bonus to a newly registered user by transferring the specified amount
     * from the default bank account to the recipient's account.
     *
     * @param recipientIban the IBAN of the recipient's account
     * @param amount        the amount of the welcome bonus to be transferred
     */
    //TODO: Write down all exceptions that can be thrown.
    @Override
    public void grantWelcomeBonus(@NonNull Iban recipientIban, @NonNull BigDecimal amount) {
        Account bank = getAccount(DEFAULT_BANK_IBAN);
        log.trace("Gathered bank account");
        Account recipient = getAccount(recipientIban);
        log.trace("Gathered recipient account");
        Transaction transaction = buildWelcomeBonusTransaction(recipient, bank, amount);
        log.trace("Built welcome bonus transaction");
        Transaction registeredTransaction = transactionService.registerTransaction(transaction);
        log.trace("Registered welcome bonus transaction");
        transactionService.processTransactionById(registeredTransaction.getId());
        log.trace("Processed welcome bonus transaction");

    }

    private Account getAccount(Iban iban) {
        return accountService.getAccountByIban(iban);
    }

    private Transaction buildWelcomeBonusTransaction(Account recipientAccount, Account bankAccount, BigDecimal amount) {
        return Transaction.buildTransfer()
                .from(bankAccount)
                .to(recipientAccount)
                .asInternalTransfer()
                .withAmount(amount)
                .withTitle(DEFAULT_TITLE)
                .build();
    }
}
