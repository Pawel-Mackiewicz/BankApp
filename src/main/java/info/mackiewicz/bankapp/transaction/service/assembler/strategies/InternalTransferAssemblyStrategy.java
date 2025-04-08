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
     * Assembles a Transaction for an internal transfer based on the provided request.
     * <p>
     * The method logs the transfer details, retrieves the source and destination accounts,
     * resolves the transaction type from the request, and delegates transaction assembly
     * to the superclass to create the final Transaction object.
     * </p>
     *
     * @param request the internal transfer request containing transfer details
     * @return the constructed Transaction representing the internal transfer
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
     * Logs details of an internal transfer request.
     *
     * <p>
     * Extracts and logs the source IBAN, transfer amount, and recipient information (email if available,
     * otherwise IBAN) from the provided web transfer request.
     * </p>
     *
     * @param request the web transfer request containing internal transfer details
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
     * Retrieves the source account for an internal transfer based on the source IBAN provided in the web transfer request.
     *
     * @param request the web transfer request containing the source IBAN
     * @return the account corresponding to the provided source IBAN
     */
    @Override
    protected <T extends WebTransferRequest> Account getSourceAccount(T request) {
        log.debug("Finding source account by IBAN: {}", request.getSourceIban());
        Account sourceAccount = accountService.getAccountByIban(request.getSourceIban());
        log.debug("Source account found with ID: {}", sourceAccount.getId());
        return sourceAccount;
    }

    /**
     * Retrieves the destination account based on the provided web transfer request.
     * <p>
     * This method casts the web transfer request to an internal transfer request and resolves
     * the corresponding destination account.
     * </p>
     *
     * @param <T> the type of web transfer request (extending WebTransferRequest)
     * @param request the web transfer request carrying recipient details for the transfer
     * @return the resolved destination Account
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
     * Retrieves the destination account using the recipient's IBAN from the internal transfer request.
     *
     * This method logs the IBAN resolution process, queries the account service to obtain the account associated
     * with the provided recipient IBAN, and logs the resolved account's ID before returning it.
     *
     * @param request the internal transfer request containing the recipient's IBAN
     * @return the destination account corresponding to the recipient's IBAN
     */
    private Account findAccountByIban(InternalTransferRequest request) {
        log.debug("Resolving destination account by IBAN: {}", request.getRecipientIban());
        Account account = accountService.getAccountByIban(request.getRecipientIban());
        log.debug("Destination account found by IBAN with ID: {}", account.getId());
        return account;
    }

    /**
     * Retrieves the destination account based on the recipient's email provided in the transfer request.
     *
     * <p>This method logs the email used for lookup, uses the account service to retrieve the account
     * corresponding to that email, logs the account ID upon a successful retrieval, and then returns the account.
     *
     * @param request the internal transfer request containing the recipient's email
     * @return the account corresponding to the provided recipient email
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