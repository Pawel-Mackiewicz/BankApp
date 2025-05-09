package info.mackiewicz.bankapp.presentation.dashboard.service.transfer.assembler.strategies;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.account.service.AccountService;
import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import info.mackiewicz.bankapp.presentation.dashboard.dto.OwnTransferRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.WebTransferRequest;
import info.mackiewicz.bankapp.presentation.dashboard.service.transfer.assembler.TransactionAssemblyStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Strategy for assembling own transfer transactions (between user's own accounts).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OwnTransferAssemblyStrategy extends BaseTransactionAssemblyStrategy implements TransactionAssemblyStrategy<OwnTransferRequest> {

    private final AccountService accountService;

    @Override
    public Transaction assembleTransaction(OwnTransferRequest request) {
        logTransferRequest(request);
        
        Account sourceAccount = getSourceAccount(request);
        Account destinationAccount = getDestinationAccount(request);

        return super.assembleTransaction((WebTransferRequest) request, sourceAccount, destinationAccount);
    }

    @Override
    protected <T extends WebTransferRequest> void logTransferRequest(T request) {
        OwnTransferRequest ownRequest = (OwnTransferRequest) request;
        log.info("Assembling own transfer from account ID: {} to account ID: {}, amount: {}",
                ownRequest.getSourceAccountId(), ownRequest.getDestinationAccountId(), ownRequest.getAmount());
    }

    @Override
    protected <T extends WebTransferRequest> Account getSourceAccount(T request) {
        OwnTransferRequest ownRequest = (OwnTransferRequest) request;
        log.debug("Finding source account by ID: {}", ownRequest.getSourceAccountId());
        Account sourceAccount = accountService.getAccountById(ownRequest.getSourceAccountId());
        log.debug("Source account found with ID: {}", sourceAccount.getId());
        return sourceAccount;
    }

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