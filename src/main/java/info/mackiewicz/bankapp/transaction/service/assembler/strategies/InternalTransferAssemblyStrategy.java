package info.mackiewicz.bankapp.transaction.service.assembler.strategies;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.presentation.dashboard.dto.InternalTransferRequest;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.transaction.service.assembler.TransactionAssemblyStrategy;
import info.mackiewicz.bankapp.transaction.service.assembler.TransactionTypeResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Strategy for assembling internal transfer transactions.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InternalTransferAssemblyStrategy implements TransactionAssemblyStrategy<InternalTransferRequest> {

    private final AccountService accountService;

    @Override
    public Transaction assembleTransaction(InternalTransferRequest request) {
        log.info("Assembling internal transfer from IBAN: {}, amount: {}",
                request.getSourceIban(), request.getAmount());

        log.debug("Finding source account by IBAN: {}", request.getSourceIban());
        Account sourceAccount = accountService.findAccountByIban(request.getSourceIban());
        log.debug("Source account found: {}", sourceAccount.getId());

        log.debug("Resolving destination account");
        Account destinationAccount = resolveDestinationAccount(request);
        log.debug("Destination account resolved: {}", destinationAccount.getId());

        log.debug("Resolving transaction type");
        TransactionType resolvedType = TransactionTypeResolver.resolveTransactionType(request);
        log.debug("Transaction type resolved as: {}", resolvedType);

        log.debug("Building transaction with amount: {}", request.getAmount());
        Transaction transaction = Transaction.buildTransfer()
                .from(sourceAccount)
                .to(destinationAccount)
                .withAmount(new BigDecimal(request.getAmount()))
                .withTitle(request.getTitle())
                .withTransactionType(resolvedType)
                .build();

        log.info("Internal transfer transaction assembled successfully with ID: {}", transaction.getId());
        return transaction;
    }

    @Override
    public Class<InternalTransferRequest> getSupportedRequestType() {
        return InternalTransferRequest.class;
    }

    private Account resolveDestinationAccount(InternalTransferRequest request) {
        if (request.getRecipientEmail() != null) {
            log.debug("Resolving destination account by email: {}", request.getRecipientEmail());
            Account account = accountService.findAccountByOwnersEmail(request.getRecipientEmail());
            log.debug("Destination account found by email with ID: {}", account.getId());
            return account;
        } else {
            log.debug("Resolving destination account by IBAN: {}", request.getRecipientIban());
            Account account = accountService.findAccountByIban(request.getRecipientIban());
            log.debug("Destination account found by IBAN with ID: {}", account.getId());
            return account;
        }
    }
}