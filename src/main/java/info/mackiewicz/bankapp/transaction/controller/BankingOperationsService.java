package info.mackiewicz.bankapp.transaction.controller;

import java.util.function.Supplier;

import org.iban4j.Iban;
import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.account.exception.AccountNotFoundByIbanException;
import info.mackiewicz.bankapp.account.exception.AccountOwnershipException;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountSecurityService;
import info.mackiewicz.bankapp.account.service.interfaces.AccountServiceInterface;
import info.mackiewicz.bankapp.shared.service.TransactionBuilderService;
import info.mackiewicz.bankapp.transaction.exception.TransactionBuildingException;
import info.mackiewicz.bankapp.transaction.exception.TransactionValidationException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.dto.BankingOperationRequest;
import info.mackiewicz.bankapp.transaction.model.dto.EmailTransferRequest;
import info.mackiewicz.bankapp.transaction.model.dto.IbanTransferRequest;
import info.mackiewicz.bankapp.transaction.model.dto.TransferResponse;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
import info.mackiewicz.bankapp.user.model.interfaces.UserDetailsWithId;
import info.mackiewicz.bankapp.user.model.vo.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankingOperationsService implements BankingOperationsServiceInterface {

    private final AccountServiceInterface accountService;
    private final TransactionBuilderService transactionBuilderService;
    private final AccountSecurityService accountSecurityService;
    private final TransactionService transactionService;

    @Override
    public TransferResponse handleEmailTransfer(EmailTransferRequest transferRequest, UserDetailsWithId user) {
        log.info("Handling transfer from :{} to {}", transferRequest.getSourceIban(),
                transferRequest.getDestinationEmail());

        return handleTransfer(transferRequest,
                user.getId(),
                transferRequest.getSourceIban(),
                () -> retrieveAccountFromEmail(transferRequest.getDestinationEmail()));
    }

    private Account retrieveAccountFromEmail(Email destinationEmail) {
        return accountService.getAccountByOwnersEmail(destinationEmail);
    }

    @Override
    public TransferResponse handleIbanTransfer(IbanTransferRequest transferRequest, UserDetailsWithId user) {
        log.info("Handling transfer from :{} to {}", transferRequest.getSourceIban(),
                transferRequest.getRecipientIban());

        return handleTransfer(transferRequest,
                user.getId(),
                transferRequest.getSourceIban(),
                () -> retrieveAccountFromIban(transferRequest.getRecipientIban()));
    }

    private Account retrieveAccountFromIban(Iban accountIban) {
        return accountService.getAccountByIban(accountIban);
    }

    private TransferResponse handleTransfer(
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
