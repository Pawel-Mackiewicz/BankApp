package info.mackiewicz.bankapp.transaction.service.assembler.strategies;

import java.math.BigDecimal;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.presentation.dashboard.dto.WebTransferRequest;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public abstract class BaseTransactionAssemblyStrategy {

    /**
     * Assembles a Transaction using the provided transfer request and account details.
     *
     * This method delegates the creation of a Transaction to the buildTransfer method,
     * utilizing the given web transfer request, source account, destination account, and
     * resolved transaction type.
     *
     * @param request the web transfer request containing transaction details
     * @param sourceAccount the account from which funds are debited
     * @param destinationAccount the account to which funds are credited
     * @param resolvedType the type of transaction to be assembled
     * @return the constructed Transaction
     */
    protected <T extends WebTransferRequest> Transaction assembleTransaction(
            T request,
            Account sourceAccount,
            Account destinationAccount,
            TransactionType resolvedType) {

        return buildTransfer(request, sourceAccount, destinationAccount, resolvedType);
    }

    /**
     * Constructs a Transaction object based on the provided transfer request and account details.
     *
     * <p>
     * This method logs the transfer amount from the request, then uses a builder pattern to create a Transaction
     * by setting the source and destination accounts, transferring the amount (converted to BigDecimal), applying
     * the title from the request, and assigning the specified transaction type. It logs the assembled transaction's ID
     * upon successful creation.
     * </p>
     *
     * @param <T> the type of WebTransferRequest containing the transfer details
     * @param request the web transfer request with transaction details such as amount and title
     * @param sourceAccount the account from which funds are transferred
     * @param destinationAccount the account receiving the funds
     * @param resolvedType the type of the transaction
     * @return the assembled Transaction object
     */
    protected <T extends WebTransferRequest> Transaction buildTransfer(T request, Account sourceAccount,
            Account destinationAccount, TransactionType resolvedType) {
        log.debug("Building transaction with amount: {}", request.getAmount());

        Transaction transaction = Transaction.buildTransfer()
                .from(sourceAccount)
                .to(destinationAccount)
                .withAmount(new BigDecimal(request.getAmount()))
                .withTitle(request.getTitle())
                .withTransactionType(resolvedType)
                .build();
        log.info("Transfer transaction assembled successfully with ID: {}", transaction.getId());

        return transaction;
    }

    /**
 * Logs details of the provided web transfer request.
 *
 * <p>Subclasses must implement this method to record relevant information from the web transfer request,
 * aiding in auditing and debugging.
 *
 * @param request the web transfer request to be logged
 */
protected abstract <T extends WebTransferRequest> void logTransferRequest(T request);

    /**
 * Retrieves the source account from the provided web transfer request.
 *
 * @param request the web transfer request containing account details
 * @return the account to be used as the source in a transaction
 */
protected abstract <T extends WebTransferRequest> Account getSourceAccount(T request);

    /**
 * Retrieves the destination account from the provided web transfer request.
 *
 * <p>This abstract method should be implemented by subclasses to extract the destination account details
 * from the given transfer request.</p>
 *
 * @param request the web transfer request containing account transfer details
 * @return the destination account associated with the transfer request
 */
protected abstract <T extends WebTransferRequest> Account getDestinationAccount(T request);

}
