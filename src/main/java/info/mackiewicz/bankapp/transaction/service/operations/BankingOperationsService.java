package info.mackiewicz.bankapp.transaction.service.operations;

import java.util.function.Supplier;

import org.iban4j.Iban;
import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.account.exception.AccountNotFoundByIbanException;
import info.mackiewicz.bankapp.account.exception.AccountOwnershipException;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountSecurityService;
import info.mackiewicz.bankapp.shared.service.TransactionBuilderService;
import info.mackiewicz.bankapp.transaction.exception.InvalidIbanException;
import info.mackiewicz.bankapp.transaction.exception.TransactionBuildingException;
import info.mackiewicz.bankapp.transaction.exception.TransactionValidationException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.dto.BankingOperationRequest;
import info.mackiewicz.bankapp.transaction.model.dto.TransferResponse;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
class BankingOperationsService {

    private final TransactionBuilderService transactionBuilderService;
    private final AccountSecurityService accountSecurityService;
    private final TransactionService transactionService;

           /**
     * Handles the transfer of funds between accounts.
     *
     * @param request                The request containing transfer details
     * @param userId                 The ID of the user
     * @param sourceIban             The Iban of the source account
     * @param destinationAccountSupplier A supplier for the destination account
     * @return A response containing details of the transfer
     * @throws AccountNotFoundByIbanException if no account is found with the given Iban
     * @throws AccountOwnershipException      if the user does not own the source account
     * @throws TransactionBuildingException   if the transaction cannot be built
     * @throws TransactionValidationException if the transaction fails validation
     */
    
    public TransferResponse handleTransfer(
            BankingOperationRequest request,
            Integer userId,
            Iban sourceIban,
            Supplier<Account> destinationAccountSupplier) {

        Account validatedSourceAccount = retrieveValidatedAccount(userId, sourceIban);
        Account destinationAccount = destinationAccountSupplier.get();

        Transaction transfer = createTransferTransaction(request, validatedSourceAccount, destinationAccount);
        log.info("Transaction with ID: {} created", transfer.getId());
        
        return new TransferResponse(
            validatedSourceAccount,
            destinationAccount,
            transfer
        );
    }

    /**
     * Validates the ownership of the account using the user ID and Iban.
     *
     * @param userId      The ID of the user
     * @param accountIban The Iban of the account to validate
     * @return The validated account
     * @throws AccountOwnershipException      if the user does not own the account
     * @throws AccountNotFoundByIbanException if no account is found with the given
     *                                        Iban
     */
    private Account retrieveValidatedAccount(Integer userId, Iban accountIban) {
        return accountSecurityService.validateAccountOwnership(userId, accountIban);
    }

    /**
     * Creates a transfer transaction using the transfer request and accounts.
     * Transaction is registered in the system.
     *
     * @param transferRequest    The request containing transfer details
     * @param sourceAccount      The source account for the transfer
     * @param destinationAccount The destination account for the transfer
     * @return The created transaction
     * @throws TransactionBuildingException   if the transaction cannot be built
     * @throws TransactionValidationException if the transaction fails validation
     */
    private Transaction createTransferTransaction(BankingOperationRequest transferRequest, Account sourceAccount,
            Account destinationAccount) {
        Transaction transaction = transactionBuilderService.buildTransferTransaction(
                transferRequest.getAmount(),
                transferRequest.getTitle(),
                sourceAccount,
                destinationAccount);
        return transactionService.registerTransaction(transaction);
    }

}
