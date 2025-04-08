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
     * Assembles a transaction using the provided transfer request and account details.
     * 
     * <p>This method delegates transaction construction to {@code buildTransfer} by passing along
     * the transfer request, source account, destination account, and the resolved transaction type.
     *
     * @param request the transfer request containing the necessary transaction details
     * @param sourceAccount the account from which funds will be transferred
     * @param destinationAccount the account to which funds will be transferred
     * @param resolvedType the type of transaction being processed
     * @return the assembled transaction
     */
    protected <T extends WebTransferRequest> Transaction assembleTransaction(
            T request,
            Account sourceAccount,
            Account destinationAccount,
            TransactionType resolvedType) {

        return buildTransfer(request, sourceAccount, destinationAccount, resolvedType);
    }

    /**
     * Constructs a Transaction using the provided transfer request and account details.
     *
     * <p>This method logs the transfer amount from the request and assembles a Transaction through a builder,
     * setting the source account, destination account, amount (converted to BigDecimal), title, and the specified transaction type.
     *
     * @param request the transfer request containing transaction details such as amount and title
     * @param sourceAccount the account from which funds are debited
     * @param destinationAccount the account to which funds are credited
     * @param resolvedType the specific transaction type for processing the transfer
     * @return the assembled Transaction instance
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
 * Logs details of the WebTransferRequest.
 *
 * <p>Subclasses must implement this method to record pertinent information from the transfer request,
 * ensuring that relevant details are captured for auditing or debugging purposes.</p>
 *
 * @param <T> the type of WebTransferRequest being logged
 * @param request the transfer request containing the data to be logged
 */
protected abstract <T extends WebTransferRequest> void logTransferRequest(T request);

    /**
 * Retrieves the source account associated with the given web transfer request.
 *
 * <p>Subclasses should implement this method to extract and return the source {@link Account}
 * from the provided {@code WebTransferRequest}.</p>
 *
 * @param request the web transfer request containing account transfer information
 * @return the source account for the transaction
 */
protected abstract <T extends WebTransferRequest> Account getSourceAccount(T request);

    /**
 * Retrieves the destination account from the provided web transfer request.
 *
 * <p>Subclasses must implement this method to extract the destination account details
 * from the web transfer request based on the application's transaction processing logic.</p>
 *
 * @param request the web transfer request containing transfer details
 * @return the destination account involved in the transaction
 */
protected abstract <T extends WebTransferRequest> Account getDestinationAccount(T request);

}
