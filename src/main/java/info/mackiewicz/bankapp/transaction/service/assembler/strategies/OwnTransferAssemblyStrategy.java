package info.mackiewicz.bankapp.transaction.service.assembler.strategies;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.presentation.dashboard.dto.OwnTransferRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.WebTransferRequest;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.transaction.service.assembler.TransactionAssemblyStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Strategy for assembling own transfer transactions (between user's own accounts).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OwnTransferAssemblyStrategy extends BaseTransactionAssemblyStrategy implements TransactionAssemblyStrategy<OwnTransferRequest> {

    private final AccountService accountService;

    /**
     * Assembles a transaction for an own transfer.
     *
     * This method logs the provided own transfer request, retrieves the source and destination accounts,
     * and sets the transaction type to TRANSFER_OWN. It then delegates the transaction assembly to the superclass,
     * casting the request to a {@code WebTransferRequest} as required.
     *
     * @param request the own transfer request containing the transfer details
     * @return the assembled Transaction representing the own transfer
     */
    @Override
    public Transaction assembleTransaction(OwnTransferRequest request) {
        logTransferRequest(request);
        
        Account sourceAccount = getSourceAccount(request);
        Account destinationAccount = getDestinationAccount(request);
        TransactionType resolvedType = TransactionType.TRANSFER_OWN;
        
        return super.assembleTransaction((WebTransferRequest) request, sourceAccount, destinationAccount, resolvedType);
    }

    /**
     * Logs the details of an own transfer request.
     *
     * <p>This method casts the generic {@code WebTransferRequest} to an {@code OwnTransferRequest}
     * and logs the source account ID, destination account ID, and transfer amount to aid in debugging
     * and transaction assembly audits.</p>
     *
     * @param request the transfer request containing own transfer details
     */
    @Override
    protected <T extends WebTransferRequest> void logTransferRequest(T request) {
        OwnTransferRequest ownRequest = (OwnTransferRequest) request;
        log.info("Assembling own transfer from account ID: {} to account ID: {}, amount: {}",
                ownRequest.getSourceAccountId(), ownRequest.getDestinationAccountId(), ownRequest.getAmount());
    }

    /**
     * Retrieves the source account for an own transfer.
     *
     * <p>
     * Casts the generic transfer request to an OwnTransferRequest to extract the source account ID and
     * returns the corresponding account from the account service.
     * </p>
     *
     * @param request the web transfer request containing the source account identifier
     * @return the source account corresponding to the extracted source account ID
     */
    @Override
    protected <T extends WebTransferRequest> Account getSourceAccount(T request) {
        OwnTransferRequest ownRequest = (OwnTransferRequest) request;
        log.debug("Finding source account by ID: {}", ownRequest.getSourceAccountId());
        Account sourceAccount = accountService.getAccountById(ownRequest.getSourceAccountId());
        log.debug("Source account found with ID: {}", sourceAccount.getId());
        return sourceAccount;
    }

    /**
     * Retrieves the destination account for an own transfer.
     *
     * <p>This method casts the provided transfer request to an OwnTransferRequest, extracts the destination account ID,
     * and then uses the account service to retrieve the associated account. Debug logs record both the search and the
     * successful retrieval of the account.
     *
     * @param request the web transfer request containing the destination account ID (expected to be an instance of OwnTransferRequest)
     * @return the destination Account corresponding to the transfer request
     */
    @Override
    protected <T extends WebTransferRequest> Account getDestinationAccount(T request) {
        OwnTransferRequest ownRequest = (OwnTransferRequest) request;
        log.debug("Finding destination account by ID: {}", ownRequest.getDestinationAccountId());
        Account destinationAccount = accountService.getAccountById(ownRequest.getDestinationAccountId());
        log.debug("Destination account found with ID: {}", destinationAccount.getId());
        return destinationAccount;
    }

    @Override
    public Class<OwnTransferRequest> getSupportedRequestType() {
        return OwnTransferRequest.class;
    }
}