package info.mackiewicz.bankapp.converter;

import info.mackiewicz.bankapp.dto.InternalTransferRequest;
import info.mackiewicz.bankapp.dto.OwnTransferRequest;
import info.mackiewicz.bankapp.dto.TransferRequest;
import info.mackiewicz.bankapp.model.Account;
import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.model.TransactionBuilder;
import info.mackiewicz.bankapp.model.TransactionType;
import info.mackiewicz.bankapp.service.AccountService;
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
