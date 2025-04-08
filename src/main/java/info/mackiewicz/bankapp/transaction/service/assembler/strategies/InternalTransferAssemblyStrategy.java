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
     * Assembles an internal transfer transaction.
     *
     * This method logs the transfer request, retrieves both the source and destination accounts based on the
     * request details, and determines the appropriate transaction type. It then delegates to the superclass
     * method to complete the assembly of the transaction.
     *
     * @param request the internal transfer request containing the transactional and account detail
     * @return the assembled transaction
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
     * <p>This method logs the source IBAN, transfer amount, and recipient information for an
     * internal transfer. It logs the recipient's email if provided; otherwise, it logs the recipient's IBAN.
     * The request is expected to be an instance of InternalTransferRequest.</p>
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
     * Retrieves the source account associated with the specified transfer request.
     *
     * <p>This method looks up the account using the IBAN provided in the transfer request via the account service.</p>
     *
     * @param <T> the type of the web transfer request containing the source IBAN
     * @param request the transfer request containing the source account's IBAN
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
     * Retrieves the destination account for the internal transfer.
     *
     * <p>
     * This method casts the provided transfer request to an InternalTransferRequest and
     * resolves the destination account based on the recipient's email or IBAN.
     * </p>
     *
     * @param request the WebTransferRequest containing internal transfer details
     * @return the destination Account for the transfer
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
     * Finds the destination account using the recipient's IBAN provided in the internal transfer request.
     *
     * @param request the internal transfer request containing the recipient's IBAN
     * @return the destination account corresponding to the specified IBAN
     */
    private Account findAccountByIban(InternalTransferRequest request) {
        log.debug("Resolving destination account by IBAN: {}", request.getRecipientIban());
        Account account = accountService.getAccountByIban(request.getRecipientIban());
        log.debug("Destination account found by IBAN with ID: {}", account.getId());
        return account;
    }

    /**
     * Retrieves the destination account using the recipient's email from the provided internal transfer request.
     *
     * <p>This method extracts the recipient's email from the request and retrieves the corresponding account
     * using the account service. Debug logs capture both the email used and the found account's ID.</p>
     *
     * @param request the internal transfer request containing the recipient's email
     * @return the account associated with the recipient's email
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