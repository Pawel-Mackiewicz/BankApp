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
     * Assembles a Transaction for the external transfer request.
     *
     * <p>This method logs the transfer details, retrieves the source and destination accounts using the request's IBAN information, 
     * and determines the appropriate transaction type. It then delegates to the superclass to assemble and return the final Transaction.</p>
     *
     * @param request the external transfer request containing transfer details
     * @return the assembled Transaction representing the external transfer
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
     *
     * <p>This method logs an informational message with the source IBAN, recipient IBAN, and transfer amount
     * provided by the external transfer request.
     *
     * @param request the external transfer request containing the transfer details
     */
    @Override
    protected <T extends WebTransferRequest> void logTransferRequest(T request) {
        log.info("Assembling external transfer from IBAN: {} to IBAN: {}, amount: {}",
                request.getSourceIban(), request.getRecipientIban(), request.getAmount());
    }

    /**
     * Retrieves the source account for an external transfer transaction.
     *
     * <p>This method extracts the source IBAN from the provided {@code WebTransferRequest} and obtains the corresponding account using the account service.
     *
     * @param request the external transfer request containing the source IBAN
     * @return the account associated with the provided source IBAN
     */
    @Override
    protected <T extends WebTransferRequest> Account getSourceAccount(T request) {
        log.debug("Finding source account by IBAN: {}", request.getSourceIban());
        Account sourceAccount = accountService.getAccountByIban(request.getSourceIban());
        log.debug("Source account found with ID: {}", sourceAccount.getId());
        return sourceAccount;
    }

    /**
     * Retrieves the destination account for the given web transfer request.
     *
     * <p>
     * The destination account is obtained by searching for the recipient's IBAN provided in the transfer request.
     * </p>
     *
     * @param <T> a subtype of WebTransferRequest containing transfer details, including the recipient's IBAN
     * @param request the web transfer request with recipient IBAN information
     * @return the destination account corresponding to the recipient's IBAN
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