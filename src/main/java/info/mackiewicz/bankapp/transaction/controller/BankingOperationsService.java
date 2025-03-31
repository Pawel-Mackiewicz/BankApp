package info.mackiewicz.bankapp.transaction.controller;

import org.iban4j.Iban;
import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.account.exception.AccountOwnershipException;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.transaction.model.dto.IbanTransferRequest;
import info.mackiewicz.bankapp.transaction.model.dto.TransferResponse;
import info.mackiewicz.bankapp.user.model.interfaces.UserDetailsWithId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankingOperationsService {

    private final AccountService accountService;

    TransferResponse handleIbanTransfer(IbanTransferRequest transferRequest, UserDetailsWithId user) {
        Iban sourceIban = transferRequest.getSourceIban();
        Iban destinationIban = transferRequest.getRecipientIban();

        // Validate the account ownership
        Account sourceAccount = retrieveAccount(sourceIban);
        validateAccountOwnership(user, sourceAccount);

        Account destinationAccount = retrieveAccount(destinationIban);

        TransactionType type = resolveTransferType(sourceIban, destinationIban);
        
        Transaction transfer = buildTransferTransaction(transferRequest, sourceAccount, destinationAccount, type);

        return new TransferResponse(sourceAccount, destinationAccount, transfer);
    }

    private Account retrieveAccount(Iban accountIban) {
        // Retrieve the account using the source Iban from the transfer request
        return accountService.getAccountByIban(accountIban);
    }

    private Transaction buildTransferTransaction(IbanTransferRequest transferRequest, Account sourceAccount,
            Account destinationAccount, TransactionType type) {
        Transaction transfer = Transaction.buildTransfer()
                .from(sourceAccount)
                .to(destinationAccount)
                .withTransactionType(type)
                .withAmount(transferRequest.getAmount())
                .withTitle(transferRequest.getTitle())
                .build();
        return transfer;
    }
    
    private TransactionType resolveTransferType(Iban sourceIban, Iban destinationIban) {
        return !isSameBank(sourceIban, destinationIban) ? TransactionType.TRANSFER_EXTERNAL 
        : !isSameOwner(sourceIban, destinationIban) ? TransactionType.TRANSFER_INTERNAL
        : TransactionType.TRANSFER_OWN;
    }

    private boolean isSameOwner(Iban sourceIban, Iban destinationIban) {
        return sourceIban.getAccountNumber().regionMatches(0, destinationIban.getAccountNumber(), 0, 10);
    }

    private boolean isSameBank(Iban sourceIban, Iban destinationIban) {
        return sourceIban.getBankCode().equals(destinationIban.getBankCode())
                && sourceIban.getCountryCode().equals(destinationIban.getCountryCode());
    }

    //move to external service
    private void validateAccountOwnership(UserDetailsWithId user, Account account) {
        Integer ownerId = account.getOwner().getId();

        // Check if the user is the owner of the account
        if (!ownerId.equals(user.getId())) {
            log.warn("Unauthorized access attempt by user ID: {}", user.getId());
            throw new AccountOwnershipException("Unauthorized access to account");
        }
    }

}
