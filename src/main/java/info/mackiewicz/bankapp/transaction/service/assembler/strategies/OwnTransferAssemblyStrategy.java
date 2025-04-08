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
     * Assembles a Transaction from the provided own transfer request.
     *
     * <p>This method logs the own transfer request, retrieves the source and destination accounts,
     * sets the transaction type to {@code TRANSFER_OWN}, and delegates the transaction assembly to the
     * superclass after converting the request to a {@code WebTransferRequest}.</p>
     *
     * @param request the own transfer request containing details required for the transaction
     * @return the assembled Transaction corresponding to the own transfer
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
     * Logs details of an own transfer request.
     *
     * <p>This method extracts the source account ID, destination account ID, and transfer amount from the provided web transfer request,
     * assuming it is an instance of OwnTransferRequest, and logs the information for auditing purposes.</p>
     *
     * @param request the web transfer request containing own transfer details
     */
    @Override
    protected <T extends WebTransferRequest> void logTransferRequest(T request) {
        OwnTransferRequest ownRequest = (OwnTransferRequest) request;
        log.info("Assembling own transfer from account ID: {} to account ID: {}, amount: {}",
                ownRequest.getSourceAccountId(), ownRequest.getDestinationAccountId(), ownRequest.getAmount());
    }

    /**
     * Retrieves the source account for an own transfer transaction.
     *
     * <p>This method casts the provided transfer request to an OwnTransferRequest to extract
     * the source account ID, then uses the account service to fetch and return the corresponding account.
     *
     * @param request the transfer request containing the source account ID
     * @return the account associated with the provided source account ID
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
     * <p>This method extracts the destination account ID from the provided transfer request,
     * logs the lookup process, and returns the corresponding Account object.</p>
     *
     * @param request the WebTransferRequest containing the destination account details
     * @return the Account corresponding to the destination account ID specified in the request
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