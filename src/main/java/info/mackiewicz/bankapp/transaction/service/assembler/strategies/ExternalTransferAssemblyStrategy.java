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
     * Assembles a Transaction for an external transfer.
     *
     * <p>This method logs the details of the transfer request, retrieves both the source and destination accounts
     * using their respective IBANs, and determines the appropriate transaction type. It then delegates the complete
     * transaction assembly to its superclass.
     *
     * @param request the external transfer request containing transfer details and account IBANs
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
     * Logs the external transfer request's IBAN details and amount.
     *
     * <p>This method logs an informational message with the source IBAN, recipient IBAN, and transfer amount
     * extracted from the provided WebTransferRequest, aiding in the monitoring and debugging of external transfers.
     *
     * @param request the external transfer request containing the source IBAN, recipient IBAN, and transfer amount
     */
    @Override
    protected <T extends WebTransferRequest> void logTransferRequest(T request) {
        log.info("Assembling external transfer from IBAN: {} to IBAN: {}, amount: {}",
                request.getSourceIban(), request.getRecipientIban(), request.getAmount());
    }

    /**
     * Retrieves the source account associated with the given transfer request.
     *
     * <p>This method logs the source IBAN provided in the request, retrieves the corresponding account
     * using the account service, and logs the retrieved account's ID.</p>
     *
     * @param request the transfer request containing the source IBAN information
     * @return the Account corresponding to the source IBAN in the request
     */
    @Override
    protected <T extends WebTransferRequest> Account getSourceAccount(T request) {
        log.debug("Finding source account by IBAN: {}", request.getSourceIban());
        Account sourceAccount = accountService.getAccountByIban(request.getSourceIban());
        log.debug("Source account found with ID: {}", sourceAccount.getId());
        return sourceAccount;
    }

    /**
     * Retrieves the destination account associated with the recipient's IBAN from the transfer request.
     *
     * <p>This method logs the recipient IBAN used for retrieving the account, queries the account service for the corresponding
     * account, logs the found account's ID, and returns the account.</p>
     *
     * @param <T> the type of transfer request extending WebTransferRequest containing recipient IBAN information
     * @param request the transfer request that provides the recipient's IBAN
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