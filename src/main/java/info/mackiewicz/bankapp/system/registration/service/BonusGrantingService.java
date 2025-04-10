package info.mackiewicz.bankapp.system.registration.service;

import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
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


    void grantWelcomeBonus(@NonNull Iban recipientIban, @NonNull BigDecimal amount);
}
