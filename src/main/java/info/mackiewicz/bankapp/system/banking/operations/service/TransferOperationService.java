package info.mackiewicz.bankapp.system.banking.operations.service;

import info.mackiewicz.bankapp.account.exception.AccountNotFoundByIbanException;
import info.mackiewicz.bankapp.account.exception.AccountOwnershipException;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.system.banking.operations.api.dto.BankingOperationRequest;
import info.mackiewicz.bankapp.system.banking.operations.service.helpers.AccountSecurityService;
import info.mackiewicz.bankapp.system.banking.operations.service.helpers.TransactionBuildingService;
import info.mackiewicz.bankapp.system.banking.shared.dto.TransactionResponse;
import info.mackiewicz.bankapp.transaction.exception.TransactionBuildingException;
import info.mackiewicz.bankapp.transaction.exception.TransactionValidationException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iban4j.Iban;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferOperationService {

    private final TransactionBuildingService transactionBuilderService;
    private final AccountSecurityService accountSecurityService;
    private final TransactionService transactionService;

    /**
     * Handles the transfer of funds between accounts.
     *
     * @param request                    The request containing transfer details
     * @param userId                     The ID of the user
     * @param sourceIban                 The Iban of the source account
     * @param destinationAccountSupplier A supplier for the destination account
     * @return A response containing details of the transfer
     * @throws AccountNotFoundByIbanException if no account is found with the given
     *                                        Iban
     * @throws AccountOwnershipException      if the user does not own the source
     *                                        account
     * @throws TransactionBuildingException   if the transaction cannot be built
     * @throws TransactionValidationException if the transaction fails validation
     */

    public TransactionResponse handleTransfer(
            BankingOperationRequest request,
            Integer userId,
            Iban sourceIban,
            Supplier<Account> destinationAccountSupplier) { // ideally this should be an Iban as well, but ideally whole
                                                            // transaction system should work on IBANs not on accounts
        MDC.put("transactionId", request.getTempId().toString());
        try {
        log.info("Handling transaction: {}", request);

        log.debug("Validating source account");
        Account validatedSourceAccount = accountSecurityService.retrieveValidatedAccount(userId, sourceIban);

        log.debug("Validating destination account");
        Account destinationAccount = destinationAccountSupplier.get();
        
        log.debug("Creating transfer transaction");
        Transaction transfer = createTransferTransaction(request, validatedSourceAccount, destinationAccount);

        log.debug("Registering transfer transaction");
        Transaction registeredTransaction = transactionService.registerTransaction(transfer);

        log.info("Transaction registered with ID: {}", registeredTransaction.getId());

        //transaction processing should be called here, not in the transaction service
        return new TransactionResponse(
                validatedSourceAccount,
                destinationAccount,
                registeredTransaction);
        } finally {
            MDC.clear();
        }
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
        return transactionBuilderService.buildTransferTransaction(
                transferRequest.getAmount(),
                transferRequest.getTitle(),
                sourceAccount,
                destinationAccount);
    }

}
