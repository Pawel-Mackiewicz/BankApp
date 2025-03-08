package info.mackiewicz.bankapp.transaction.service;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.presentation.dashboard.dto.OwnTransferRequest;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Strategy for assembling own transfer transactions (between user's own accounts).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OwnTransferAssemblyStrategy implements TransactionAssemblyStrategy<OwnTransferRequest> {

    private final AccountService accountService;

    @Override
    public Transaction assembleTransaction(OwnTransferRequest request) {
        log.info("Assembling own transfer from account ID: {} to account ID: {}, amount: {}",
                request.getSourceAccountId(), request.getDestinationAccountId(), request.getAmount());

        log.debug("Finding source account by ID: {}", request.getSourceAccountId());
        Account sourceAccount = accountService.getAccountById(request.getSourceAccountId());
        log.debug("Source account found: {}", sourceAccount.getId());

        log.debug("Finding destination account by ID: {}", request.getDestinationAccountId());
        Account destinationAccount = accountService.getAccountById(request.getDestinationAccountId());
        log.debug("Destination account found: {}", destinationAccount.getId());

        log.debug("Building transaction with amount: {}", request.getAmount());
        Transaction transaction = Transaction.buildTransfer()
                .from(sourceAccount)
                .to(destinationAccount)
                .withAmount(new BigDecimal(request.getAmount()))
                .withTitle(request.getTitle())
                .asOwnTransfer()
                .build();

        log.info("Own transfer transaction assembled successfully with ID: {}", transaction.getId());
        return transaction;
    }

    @Override
    public Class<OwnTransferRequest> getSupportedRequestType() {
        return OwnTransferRequest.class;
    }
}