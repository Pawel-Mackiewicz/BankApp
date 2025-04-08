package info.mackiewicz.bankapp.system.banking.service.helpers;

import java.math.BigDecimal;

import org.iban4j.Iban;
import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import lombok.RequiredArgsConstructor;

/**
 * Service for building transactions.
 * This service is responsible for creating transaction objects based on the provided request and account information.
 */
@Service
@RequiredArgsConstructor
public class TransactionBuildingService {
    
    private final IbanAnalysisService ibanAnalysisService;

    /**
     * Builds a transfer transaction between two accounts.
     *
     * @param amount            The amount to be transferred
     * @param title             The title of the transaction
     * @param sourceAccount      The account from which the amount is transferred
     * @param destinationAccount The account to which the amount is transferred
     * @return A Transaction object representing the transfer
     * @throws TransactionBuildingException if the transaction cannot be built
     */
    public Transaction buildTransferTransaction(BigDecimal amount, String title, Account sourceAccount,
            Account destinationAccount) {

        TransactionType type = resolveTransferType(sourceAccount.getIban(), destinationAccount.getIban());

        return Transaction.buildTransfer()
                .from(sourceAccount)
                .to(destinationAccount)
                .withTransactionType(type)
                .withAmount(amount)
                .withTitle(title)
                .build();
    }

        /**
     * Determines the transaction type based on the source and destination IBANs.
     * <p>
     * Delegates the resolution of the transaction type to the IbanAnalysisService.
     *
     * @param sourceIban the IBAN of the source account
     * @param destinationIban the IBAN of the destination account
     * @return the transaction type as determined by the IbanAnalysisService
     */
    private TransactionType resolveTransferType(Iban sourceIban, Iban destinationIban) {
        return ibanAnalysisService.resolveTransferType(sourceIban, destinationIban);
    }
}