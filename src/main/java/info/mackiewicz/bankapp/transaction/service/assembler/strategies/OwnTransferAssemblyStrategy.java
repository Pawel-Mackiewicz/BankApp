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
     * Assembles a transaction for an own transfer between a user's accounts.
     *
     * <p>This method logs the transfer request, retrieves the source and destination accounts,
     * and delegates to the parent method to construct the transaction using a fixed type of 
     * {@code TransactionType.TRANSFER_OWN}.
     *
     * @param request the own transfer request containing the transfer details
     * @return the assembled transaction
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
     * <p>This method casts the provided generic WebTransferRequest to an OwnTransferRequest and records
     * the source account ID, destination account ID, and transfer amount.</p>
     *
     * @param request the web transfer request containing details of the own transfer
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
     * Extracts the source account ID from the provided transfer request (expected to be an instance of {@code OwnTransferRequest})
     * and returns the associated account obtained from the account service.
     * </p>
     *
     * @param request the transfer request containing the source account identifier
     * @return the {@code Account} corresponding to the source account identifier
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
     * <p>
     * Casts the provided web transfer request to an OwnTransferRequest to extract the destination account ID,
     * logs the lookup process, and fetches the corresponding account using the account service.
     * </p>
     *
     * @param request the web transfer request containing the destination account ID
     * @return the destination account associated with the specified transfer request
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