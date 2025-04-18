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

    @Override
    public Transaction assembleTransaction(InternalTransferRequest request) {
        logTransferRequest(request);
        
        Account sourceAccount = getSourceAccount(request);
        Account destinationAccount = getDestinationAccount(request);
        TransactionType resolvedType = transactionTypeResolver.resolveTransactionType(request);
        
        return super.assembleTransaction(request, sourceAccount, destinationAccount, resolvedType);
    }
    
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

    @Override
    protected <T extends WebTransferRequest> Account getSourceAccount(T request) {
        log.debug("Finding source account by IBAN: {}", request.getSourceIban());
        Account sourceAccount = accountService.getAccountByIban(request.getSourceIban());
        log.debug("Source account found with ID: {}", sourceAccount.getId());
        return sourceAccount;
    }

    @Override
    protected <T extends WebTransferRequest> Account getDestinationAccount(T request) {
        return resolveDestinationAccount((InternalTransferRequest) request);
    }
    
    private Account resolveDestinationAccount(InternalTransferRequest request) {
        return request.getRecipientEmail() != null ? 
                findAccountByEmail(request) : 
                findAccountByIban(request);
    }
    
    private Account findAccountByIban(InternalTransferRequest request) {
        log.debug("Resolving destination account by IBAN: {}", request.getRecipientIban());
        Account account = accountService.getAccountByIban(request.getRecipientIban());
        log.debug("Destination account found by IBAN with ID: {}", account.getId());
        return account;
    }

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