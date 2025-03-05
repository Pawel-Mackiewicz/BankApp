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
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionAssembler {

    private final AccountService accountService;
    private final TransactionBuilder transactionBuilder;

    public Transaction assembleExternalTransfer(TransferRequest request) {
        log.info("Assembling external transfer from IBAN: {} to IBAN: {}, amount: {}",
                request.getSourceIban(), request.getRecipientIban(), request.getAmount());

        log.debug("Finding source account by IBAN: {}", request.getSourceIban());
        Account sourceAccount = accountService.findAccountByIban(request.getSourceIban());
        log.debug("Source account found: {}", sourceAccount.getId());

        log.debug("Finding destination account by IBAN: {}", request.getRecipientIban());
        Account destinationAccount = accountService.findAccountByIban(request.getRecipientIban());
        log.debug("Destination account found: {}", destinationAccount.getId());

        log.debug("Building transaction with amount: {}", request.getAmount());
        Transaction transaction = transactionBuilder
                .withSourceAccount(sourceAccount)
                .withDestinationAccount(destinationAccount)
                .withAmount(new BigDecimal(request.getAmount()))
                .withTransactionTitle(request.getTitle())
                .withType(resolveTransactionType(request))
                .build();
        
        log.info("External transfer transaction assembled successfully with ID: {}", transaction.getId());
        return transaction;
    }

    public Transaction assembleInternalTransfer(InternalTransferRequest request) {
        log.info("Assembling internal transfer from IBAN: {}, amount: {}",
                request.getSourceIban(), request.getAmount());

        log.debug("Finding source account by IBAN: {}", request.getSourceIban());
        Account sourceAccount = accountService.findAccountByIban(request.getSourceIban());
        log.debug("Source account found: {}", sourceAccount.getId());

        log.debug("Resolving destination account");
        Account destinationAccount = resolveDestinationAccount(request);
        log.debug("Destination account resolved: {}", destinationAccount.getId());

        log.debug("Building transaction with amount: {}", request.getAmount());
        Transaction transaction = transactionBuilder
                .withSourceAccount(sourceAccount)
                .withDestinationAccount(destinationAccount)
                .withAmount(new BigDecimal(request.getAmount()))
                .withTransactionTitle(request.getTitle())
                .withType(resolveTransactionType(request))
                .build();

        log.info("Internal transfer transaction assembled successfully with ID: {}", transaction.getId());
        return transaction;
    }

    public Transaction assembleOwnTransfer(OwnTransferRequest request) {
        log.info("Assembling own transfer from account ID: {} to account ID: {}, amount: {}",
                request.getSourceAccountId(), request.getDestinationAccountId(), request.getAmount());

        log.debug("Finding source account by ID: {}", request.getSourceAccountId());
        Account sourceAccount = accountService.getAccountById(request.getSourceAccountId());
        log.debug("Source account found: {}", sourceAccount.getId());

        log.debug("Finding destination account by ID: {}", request.getDestinationAccountId());
        Account destinationAccount = accountService.getAccountById(request.getDestinationAccountId());
        log.debug("Destination account found: {}", destinationAccount.getId());

        log.debug("Building transaction with amount: {}", request.getAmount());
        Transaction transaction = transactionBuilder
                .withSourceAccount(sourceAccount)
                .withDestinationAccount(destinationAccount)
                .withAmount(new BigDecimal(request.getAmount()))
                .withTransactionTitle(request.getTitle())
                .withType(TransactionType.TRANSFER_OWN)
                .build();

        log.info("Own transfer transaction assembled successfully with ID: {}", transaction.getId());
        return transaction;
    }

    private TransactionType resolveTransactionType(TransferRequest request) {
        log.debug("Resolving transaction type for request with source IBAN: {}", request.getSourceIban());
        
        if (request.getRecipientIban() == null) {
            log.debug("Recipient IBAN is null, resolving as TRANSFER_INTERNAL");
            return TransactionType.TRANSFER_INTERNAL;
        }

        boolean isOwnTransfer = request.getSourceIban().regionMatches(5, request.getRecipientIban(), 5, 20);
        TransactionType resolvedType = isOwnTransfer ? TransactionType.TRANSFER_OWN : request.getTransactionType();
        log.debug("Transaction type resolved as: {}", resolvedType);
        return resolvedType;
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
