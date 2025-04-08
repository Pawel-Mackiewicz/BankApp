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
     * Assembles a Transaction from the provided web transfer request and account details.
     *
     * <p>This method delegates the construction of the Transaction to the {@code buildTransfer} method,
     * using the information from the web transfer request, the source and destination accounts, and the
     * resolved transaction type.</p>
     *
     * @param request the web transfer request containing transaction details
     * @param sourceAccount the account initiating the transaction
     * @param destinationAccount the account receiving the transaction
     * @param resolvedType the resolved transaction type for the transfer
     * @return the constructed Transaction based on the provided request and account data
     */
    protected <T extends WebTransferRequest> Transaction assembleTransaction(
            T request,
            Account sourceAccount,
            Account destinationAccount,
            TransactionType resolvedType) {

        return buildTransfer(request, sourceAccount, destinationAccount, resolvedType);
    }

    /**
     * Constructs a transfer transaction using details from the web transfer request.
     *
     * <p>The transaction is created by setting the source and destination accounts, converting the request
     * amount to a BigDecimal, and assigning the title and resolved transaction type from the request.
     * Debug and info logs capture the transfer amount and the assembled transaction ID respectively.
     *
     * @param request the web transfer request containing transaction details such as the amount and title
     * @param sourceAccount the account from which funds are debited
     * @param destinationAccount the account to which funds are credited
     * @param resolvedType the type of transaction determined for this transfer
     * @return the constructed Transaction object
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
 * Logs the details of a web transfer request.
 *
 * <p>Subclasses must implement this method to record the specifics of the transfer request,
 * ensuring that important transaction details are captured for auditing or debugging purposes.
 *
 * @param request the web transfer request to be logged
 */
protected abstract <T extends WebTransferRequest> void logTransferRequest(T request);

    /**
 * Retrieves the source account from the provided web transfer request.
 *
 * Implementations should extract and return the Account from which funds are to be debited,
 * based on the details contained within the web transfer request.
 *
 * @param request the web transfer request containing the necessary source account information
 * @return the Account corresponding to the source account in the transfer request
 */
protected abstract <T extends WebTransferRequest> Account getSourceAccount(T request);

    /**
 * Retrieves the destination account from the provided web transfer request.
 *
 * @param request the web transfer request containing the transaction details
 * @return the destination account associated with the request
 */
protected abstract <T extends WebTransferRequest> Account getDestinationAccount(T request);

}
