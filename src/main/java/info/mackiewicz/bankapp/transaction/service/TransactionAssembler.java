package info.mackiewicz.bankapp.transaction.service;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.presentation.dashboard.dto.InternalTransferRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.OwnTransferRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.TransferRequest;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionBuilder;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class TransactionAssembler {

    private final AccountService accountService;
    private final TransactionBuilder transactionBuilder;

    public Transaction assembleExternalTransfer(TransferRequest request) {

        Account sourceAccount = accountService.findAccountByIban(request.getSourceIban())
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));
        Account destinationAccount = accountService.findAccountByIban(request.getRecipientIban())
                .orElseThrow(() -> new IllegalArgumentException("Destination account not found"));

        return transactionBuilder
                .withSourceAccount(sourceAccount)
                .withDestinationAccount(destinationAccount)
                .withAmount(new BigDecimal(request.getAmount()))
                .withTransactionTitle(request.getTitle())
                .withType(resolveTransactionType(request))
                .build();
    }

    public Transaction assembleInternalTransfer(InternalTransferRequest request) {

        Account sourceAccount = accountService.findAccountByIban(request.getSourceIban())
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));
        Account destinationAccount = resolveDestinationAccount(request);

        return transactionBuilder
                .withSourceAccount(sourceAccount)
                .withDestinationAccount(destinationAccount)
                .withAmount(new BigDecimal(request.getAmount()))
                .withTransactionTitle(request.getTitle())
                .withType(resolveTransactionType(request))
                .build();

    }

    public Transaction assembleOwnTransfer(OwnTransferRequest request) {

        Account sourceAccount = accountService.getAccountById(request.getSourceAccountId());
        Account destinationAccount = accountService.getAccountById(request.getDestinationAccountId());

        return transactionBuilder
                .withSourceAccount(sourceAccount)
                .withDestinationAccount(destinationAccount)
                .withAmount(new BigDecimal(request.getAmount()))
                .withTransactionTitle(request.getTitle())
                .withType(TransactionType.TRANSFER_OWN)
                .build();
    }

    private TransactionType resolveTransactionType(TransferRequest request) {
        
        //TODO: IMPLEMENT LOGIC WHEN TRANSACTION IS THROUGH EMAIL
        if (request.getRecipientIban() == null) {
            return TransactionType.TRANSFER_INTERNAL;
        }

        return request.getSourceIban().regionMatches(5, request.getRecipientIban(), 5, 20)
                ? TransactionType.TRANSFER_OWN :
                request.getTransactionType();
    }

    private Account resolveDestinationAccount(InternalTransferRequest request) {
        
        if (request.getRecipientEmail() != null) {
            return accountService.findAccountByOwnersEmail(request.getRecipientEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Destination account not found by email"));
        } else {
            return accountService.findAccountByIban(request.getRecipientIban())
                    .orElseThrow(() -> new IllegalArgumentException("Destination account not found by IBAN"));
        }
    }
}
