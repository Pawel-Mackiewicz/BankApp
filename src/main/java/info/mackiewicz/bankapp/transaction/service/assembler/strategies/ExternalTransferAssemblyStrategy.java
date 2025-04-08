package info.mackiewicz.bankapp.transaction.service.assembler.strategies;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.presentation.dashboard.dto.ExternalTransferRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.WebTransferRequest;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.transaction.service.assembler.TransactionAssemblyStrategy;
import info.mackiewicz.bankapp.transaction.service.assembler.WebTransactionTypeResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Strategy for assembling external transfer transactions.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalTransferAssemblyStrategy extends BaseTransactionAssemblyStrategy implements TransactionAssemblyStrategy<ExternalTransferRequest> {

    private final AccountService accountService;
    private final WebTransactionTypeResolver transactionTypeResolver;

    /**
     * Assembles a transaction for an external transfer.
     *
     * <p>This method logs the transfer request, retrieves the source and destination accounts
     * using the request's IBANs, and resolves the transaction type before delegating to the
     * superclass method to build the complete transaction.
     *
     * @param request the external transfer request containing transfer details including IBANs and the transfer amount
     * @return the assembled transaction
     */
    @Override
    public Transaction assembleTransaction(ExternalTransferRequest request) {
        logTransferRequest(request);
        
        Account sourceAccount = getSourceAccount(request);
        Account destinationAccount = getDestinationAccount(request);
        TransactionType resolvedType = transactionTypeResolver.resolveTransactionType(request);
        
        return super.assembleTransaction(request, sourceAccount, destinationAccount, resolvedType);
    }

    /**
     * Logs the details of an external transfer request.
     * <p>
     * This method logs an informational message that includes the source IBAN, destination IBAN,
     * and transfer amount derived from the provided web transfer request. These details aid in tracing
     * and debugging external transfer operations.
     * </p>
     *
     * @param request the web transfer request containing the source and recipient IBANs and the transfer amount
     */
    @Override
    protected <T extends WebTransferRequest> void logTransferRequest(T request) {
        log.info("Assembling external transfer from IBAN: {} to IBAN: {}, amount: {}",
                request.getSourceIban(), request.getRecipientIban(), request.getAmount());
    }

    /**
     * Retrieves the source account corresponding to the given web transfer request.
     *
     * <p>This method looks up the account using the source IBAN provided in the request.</p>
     *
     * @param request the web transfer request containing the source IBAN information
     * @return the source account corresponding to the provided IBAN
     */
    @Override
    protected <T extends WebTransferRequest> Account getSourceAccount(T request) {
        log.debug("Finding source account by IBAN: {}", request.getSourceIban());
        Account sourceAccount = accountService.getAccountByIban(request.getSourceIban());
        log.debug("Source account found with ID: {}", sourceAccount.getId());
        return sourceAccount;
    }

    /**
     * Retrieves the destination account associated with the recipient IBAN in the provided web transfer request.
     *
     * @param request the web transfer request containing the recipient IBAN
     * @return the destination account corresponding to the recipient IBAN
     */
    @Override
    protected <T extends WebTransferRequest> Account getDestinationAccount(T request) {
        log.debug("Finding destination account by IBAN: {}", request.getRecipientIban());
        Account destinationAccount = accountService.getAccountByIban(request.getRecipientIban());
        log.debug("Destination account found with ID: {}", destinationAccount.getId());
        return destinationAccount;
    }

    @Override
    public Class<ExternalTransferRequest> getSupportedRequestType() {
        return ExternalTransferRequest.class;
    }
}