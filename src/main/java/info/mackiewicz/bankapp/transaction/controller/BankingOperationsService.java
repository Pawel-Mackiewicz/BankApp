package info.mackiewicz.bankapp.transaction.controller;

import org.iban4j.Iban;
import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.account.exception.AccountOwnershipException;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.shared.service.IbanAnalysisService;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.transaction.model.dto.IbanTransferRequest;
import info.mackiewicz.bankapp.transaction.model.dto.TransferResponse;
import info.mackiewicz.bankapp.transaction.service.TransactionBuilderService;
import info.mackiewicz.bankapp.user.model.interfaces.UserDetailsWithId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankingOperationsService {

    private final AccountService accountService;
    private final IbanAnalysisService ibanAnalysisService;
    private final TransactionBuilderService transactionBuilderService;

    public TransferResponse handleIbanTransfer(IbanTransferRequest transferRequest, UserDetailsWithId user) {
        Iban sourceIban = transferRequest.getSourceIban();
        Iban destinationIban = transferRequest.getRecipientIban();

        Account sourceAccount = retrieveAccount(sourceIban);
        validateAccountOwnership(user.getId(), sourceAccount);

        Account destinationAccount = retrieveAccount(destinationIban);

        TransactionType type = resolveTransferType(sourceIban, destinationIban);

        Transaction transfer = buildTransferTransaction(transferRequest, sourceAccount, destinationAccount, type);

        return new TransferResponse(sourceAccount, destinationAccount, transfer);
    }

    private Account retrieveAccount(Iban accountIban) {
        // Retrieve the account using the source Iban from the transfer request
        return accountService.getAccountByIban(accountIban);
    }
    private void validateAccountOwnership(Integer userId, Account account) {
        Integer ownerId = account.getOwner().getId();

        // Check if the user is the owner of the account
        if (!ownerId.equals(userId)) {
            log.warn("Unauthorized access attempt by user ID: {}", userId);
            throw new AccountOwnershipException("Unauthorized access to account");
        }
    }

    private TransactionType resolveTransferType(Iban sourceIban, Iban destinationIban) {
        return ibanAnalysisService.resolveTransferType(sourceIban, destinationIban);
    }

    private Transaction buildTransferTransaction(IbanTransferRequest transferRequest, Account sourceAccount,
            Account destinationAccount, TransactionType type) {
        return transactionBuilderService.buildTransferTransaction(transferRequest, sourceAccount,
                destinationAccount, type);
    }


}
