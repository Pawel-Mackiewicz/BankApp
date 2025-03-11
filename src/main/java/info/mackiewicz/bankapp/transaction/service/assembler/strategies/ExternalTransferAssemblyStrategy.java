package info.mackiewicz.bankapp.transaction.service.assembler.strategies;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.presentation.dashboard.dto.ExternalTransferRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.TransferRequest;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.transaction.service.assembler.TransactionAssemblyStrategy;
import info.mackiewicz.bankapp.transaction.service.assembler.TransactionTypeResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Strategy for assembling external transfer transactions.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalTransferAssemblyStrategy extends BaseTransactionAssemblyStrategy implements TransactionAssemblyStrategy<ExternalTransferRequest> {

    private final AccountService accountService;
    private final TransactionTypeResolver transactionTypeResolver;

    @Override
    public Transaction assembleTransaction(ExternalTransferRequest request) {
        logTransferRequest(request);
        
        Account sourceAccount = getSourceAccount(request);
        Account destinationAccount = getDestinationAccount(request);
        TransactionType resolvedType = transactionTypeResolver.resolveTransactionType(request);
        
        return super.assembleTransaction(request, sourceAccount, destinationAccount, resolvedType);
    }

    @Override
    protected <T extends TransferRequest> void logTransferRequest(T request) {
        log.info("Assembling external transfer from IBAN: {} to IBAN: {}, amount: {}",
                request.getSourceIban(), request.getRecipientIban(), request.getAmount());
    }

    @Override
    protected <T extends TransferRequest> Account getSourceAccount(T request) {
        log.debug("Finding source account by IBAN: {}", request.getSourceIban());
        Account sourceAccount = accountService.findAccountByIban(request.getSourceIban());
        log.debug("Source account found with ID: {}", sourceAccount.getId());
        return sourceAccount;
    }

    @Override
    protected <T extends TransferRequest> Account getDestinationAccount(T request) {
        log.debug("Finding destination account by IBAN: {}", request.getRecipientIban());
        Account destinationAccount = accountService.findAccountByIban(request.getRecipientIban());
        log.debug("Destination account found with ID: {}", destinationAccount.getId());
        return destinationAccount;
    }

    @Override
    public Class<ExternalTransferRequest> getSupportedRequestType() {
        return ExternalTransferRequest.class;
    }
}