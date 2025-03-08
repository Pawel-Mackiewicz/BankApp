package info.mackiewicz.bankapp.transaction.service.assembler;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.presentation.dashboard.dto.TransferRequest;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Strategy for assembling external transfer transactions.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalTransferAssemblyStrategy implements TransactionAssemblyStrategy<TransferRequest> {

    private final AccountService accountService;

    @Override
    public Transaction assembleTransaction(TransferRequest request) {
        log.info("Assembling external transfer from IBAN: {} to IBAN: {}, amount: {}",
                request.getSourceIban(), request.getRecipientIban(), request.getAmount());

        log.debug("Finding source account by IBAN: {}", request.getSourceIban());
        Account sourceAccount = accountService.findAccountByIban(request.getSourceIban());
        log.debug("Source account found: {}", sourceAccount.getId());

        log.debug("Finding destination account by IBAN: {}", request.getRecipientIban());
        Account destinationAccount = accountService.findAccountByIban(request.getRecipientIban());
        log.debug("Destination account found: {}", destinationAccount.getId());

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

        log.info("External transfer transaction assembled successfully with ID: {}", transaction.getId());
        return transaction;
    }

    @Override
    public Class<TransferRequest> getSupportedRequestType() {
        return TransferRequest.class;
    }

}