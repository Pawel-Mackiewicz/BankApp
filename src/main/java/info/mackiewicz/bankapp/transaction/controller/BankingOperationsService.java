package info.mackiewicz.bankapp.transaction.controller;

import org.iban4j.Iban;
import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.account.exception.AccountNotFoundByIbanException;
import info.mackiewicz.bankapp.account.exception.AccountOwnershipException;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountSecurityService;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.shared.exception.IbanAnalysisException;
import info.mackiewicz.bankapp.shared.service.TransactionBuilderService;
import info.mackiewicz.bankapp.transaction.exception.TransactionBuildingException;
import info.mackiewicz.bankapp.transaction.exception.TransactionValidationException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.dto.IbanTransferRequest;
import info.mackiewicz.bankapp.transaction.model.dto.TransferResponse;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
import info.mackiewicz.bankapp.user.model.interfaces.UserDetailsWithId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankingOperationsService {

    private final AccountService accountService;
    private final TransactionBuilderService transactionBuilderService;
    private final AccountSecurityService accountSecurityService;
    private final TransactionService transactionService;

    /**
     * Handles the transfer of funds between accounts using IBANs.
     *
     * @param transferRequest The request containing transfer details
     * @param user            The user initiating the transfer
     * @return A response containing details of the transfer
     * @throws AccountNotFoundByIbanException if no account is found with the given Iban
     * @throws AccountOwnershipException      if the user does not own the source account
     * @throws TransactionBuildingException   if the transaction cannot be built
     * @throws TransactionValidationException if the transaction fails validation
     * @throws IbanAnalysisException          if there is an error analyzing the IBANs
     * 
     */
    public TransferResponse handleIbanTransfer(IbanTransferRequest transferRequest, UserDetailsWithId user) {
        log.info("Handling transfer from :{} to {}", transferRequest.getSourceIban(), transferRequest.getRecipientIban());

        Iban sourceIban = transferRequest.getSourceIban();
        Iban destinationIban = transferRequest.getRecipientIban();

        Account validatedSourceAccount = retrieveValidatedAccount(user.getId(), sourceIban);

        Account destinationAccount = retrieveAccount(destinationIban);

        Transaction transfer = createTransferTransaction(transferRequest, validatedSourceAccount, destinationAccount);

        log.info("Transfer transaction created: {}", transfer.getId());
        return new TransferResponse(validatedSourceAccount, destinationAccount, transfer);
    }

    /**
     * Retrieves the account using the source Iban from the transfer request.
     *
     * @param accountIban The Iban of the account to retrieve
     * @return The account associated with the given Iban
     * @throws AccountNotFoundByIbanException if no account is found with the given Iban
     */
    private Account retrieveAccount(Iban accountIban) {
        // Retrieve the account using the source Iban from the transfer request
        return accountService.getAccountByIban(accountIban);
    }

    /**
     * Validates the ownership of the account using the user ID and Iban.
     *
     * @param userId      The ID of the user
     * @param accountIban The Iban of the account to validate
     * @return The validated account
     * @throws AccountOwnershipException if the user does not own the account
     * @throws AccountNotFoundByIbanException if no account is found with the given Iban
     */
    private Account retrieveValidatedAccount(Integer userId,  Iban accountIban) {
        return accountSecurityService.validateAccountOwnership(userId, accountIban);
    }

    /**
     * Creates a transfer transaction using the transfer request and accounts.
     * Transaction is registered in the system.
     *
     * @param transferRequest The request containing transfer details
     * @param sourceAccount   The source account for the transfer
     * @param destinationAccount The destination account for the transfer
     * @return The created transaction
     * @throws TransactionBuildingException if the transaction cannot be built
     * @throws TransactionValidationException if the transaction fails validation
     */
    private Transaction createTransferTransaction(IbanTransferRequest transferRequest, Account sourceAccount,
            Account destinationAccount) {

        Transaction transaction =  transactionBuilderService.buildTransferTransaction(
            transferRequest.getAmount(), 
            transferRequest.getTitle(), 
            sourceAccount,
            destinationAccount
            );
        
        return transactionService.registerTransaction(transaction);
    }


}
