package info.mackiewicz.bankapp.system.registration.service;

import info.mackiewicz.bankapp.core.account.service.AccountService;
import info.mackiewicz.bankapp.core.transaction.service.TransactionService;
import lombok.NonNull;
import org.iban4j.Iban;

import java.math.BigDecimal;

/**
 * Default implementation of the BonusGrantingService for managing the granting of welcome bonuses to newly registered users.
 * This service facilitates the transfer of a predefined amount from a default bank account to the recipient's account as a welcome bonus.
 * <p>
 * Responsibilities:
 * - Retrieve the default bank account and recipient account.
 * - Create a welcome bonus transaction with a predefined title.
 * - Register and process the welcome bonus transaction.
 * <p>
 * Dependencies:
 * - {@link AccountService} for retrieving account information.
 * - {@link TransactionService} for handling transaction creation, registration, and processing.
 */
public interface BonusGrantingService {

    /**
     * Grants a welcome bonus to a newly registered user by transferring the specified amount
     * from the bank account to the recipient’s account.
     *
     * @param recipientIban the IBAN of the recipient's account
     * @param amount        the amount of the welcome bonus to be transferred
     */
    void grantWelcomeBonus(@NonNull Iban recipientIban, @NonNull BigDecimal amount);
}
