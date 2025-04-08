package info.mackiewicz.bankapp.transaction.service.assembler.strategies;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.presentation.dashboard.dto.InternalTransferRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.WebTransferRequest;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.transaction.service.assembler.TransactionAssemblyStrategy;
import info.mackiewicz.bankapp.transaction.service.assembler.WebTransactionTypeResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Strategy for assembling internal transfer transactions.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InternalTransferAssemblyStrategy extends BaseTransactionAssemblyStrategy implements TransactionAssemblyStrategy<InternalTransferRequest> {

    private final AccountService accountService;
    private final WebTransactionTypeResolver transactionTypeResolver;

    /**
     * Assembles a Transaction for an internal transfer.
     * 
     * <p>This method logs the transfer request, retrieves the associated source and destination accounts,
     * and resolves the transaction type before delegating to the superclass to create the Transaction object.</p>
     *
     * @param request the internal transfer request containing transfer details such as source and destination information
     * @return the assembled Transaction representing the internal transfer
     */
    @Override
    public Transaction assembleTransaction(InternalTransferRequest request) {
        logTransferRequest(request);
        
        Account sourceAccount = getSourceAccount(request);
        Account destinationAccount = getDestinationAccount(request);
        TransactionType resolvedType = transactionTypeResolver.resolveTransactionType(request);
        
        return super.assembleTransaction(request, sourceAccount, destinationAccount, resolvedType);
    }
    
    /**
     * Logs the internal transfer request details.
     * <p>
     * This method casts the provided {@code WebTransferRequest} to an {@code InternalTransferRequest}
     * to extract and log key details such as the source IBAN, transfer amount, and recipient information.
     * If the recipient email is present, it is logged as "email"; otherwise, the recipient IBAN is logged.
     * </p>
     *
     * @param request the web transfer request containing the internal transfer information
     */
    @Override
    protected <T extends WebTransferRequest> void logTransferRequest(T request) {
        InternalTransferRequest internalRequest = (InternalTransferRequest) request;
        log.info("Assembling internal transfer from IBAN: {}, amount: {}, recipient: {}",
                internalRequest.getSourceIban(), 
                internalRequest.getAmount(),
                internalRequest.getRecipientEmail() != null ? 
                    "email: " + internalRequest.getRecipientEmail() : 
                    "IBAN: " + internalRequest.getRecipientIban());
    }

    /**
     * Retrieves the source account based on the IBAN specified in the transfer request.
     *
     * @param request the web transfer request containing the source IBAN
     * @return the account corresponding to the provided IBAN
     */
    @Override
    protected <T extends WebTransferRequest> Account getSourceAccount(T request) {
        log.debug("Finding source account by IBAN: {}", request.getSourceIban());
        Account sourceAccount = accountService.getAccountByIban(request.getSourceIban());
        log.debug("Source account found with ID: {}", sourceAccount.getId());
        return sourceAccount;
    }

    /**
     * Retrieves the destination account from the provided transfer request.
     * <p>
     * This method casts the given WebTransferRequest to an InternalTransferRequest
     * and delegates the resolution of the destination account to {@link #resolveDestinationAccount(InternalTransferRequest)}.
     * </p>
     *
     * @param request the transfer request containing destination account details
     * @return the resolved destination account
     */
    @Override
    protected <T extends WebTransferRequest> Account getDestinationAccount(T request) {
        return resolveDestinationAccount((InternalTransferRequest) request);
    }
    
    private Account resolveDestinationAccount(InternalTransferRequest request) {
        return request.getRecipientEmail() != null ? 
                findAccountByEmail(request) : 
                findAccountByIban(request);
    }
    
    /**
     * Retrieves the destination account corresponding to the recipient's IBAN in the given internal transfer request.
     *
     * @param request the internal transfer request containing the recipient's IBAN
     * @return the account associated with the provided IBAN
     */
    private Account findAccountByIban(InternalTransferRequest request) {
        log.debug("Resolving destination account by IBAN: {}", request.getRecipientIban());
        Account account = accountService.getAccountByIban(request.getRecipientIban());
        log.debug("Destination account found by IBAN with ID: {}", account.getId());
        return account;
    }

    /**
     * Finds the destination account associated with the recipient's email address.
     *
     * <p>This method extracts the recipient's email from the provided internal transfer request,
     * queries the account service to retrieve the corresponding account, and logs the process.</p>
     *
     * @param request the internal transfer request containing the recipient's email
     * @return the destination account linked to the recipient's email
     */
    private Account findAccountByEmail(InternalTransferRequest request) {
        log.debug("Resolving destination account by email: {}", request.getRecipientEmail());
        Account account = accountService.getAccountByOwnersEmail(request.getRecipientEmail());
        log.debug("Destination account found by email with ID: {}", account.getId());
        return account;
    }

    @Override
    public Class<InternalTransferRequest> getSupportedRequestType() {
        return InternalTransferRequest.class;
    }
}