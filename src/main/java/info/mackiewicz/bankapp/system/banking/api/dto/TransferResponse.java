package info.mackiewicz.bankapp.system.banking.api.dto;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.model.adapter.AccountInfoAdapter;
import info.mackiewicz.bankapp.account.model.interfaces.AccountInfo;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.adapter.TransactionInfoAdapter;
import info.mackiewicz.bankapp.transaction.model.interfaces.TransactionInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * Class representing the response of a transfer operation.
 * This class contains information about the source and target accounts,
 * as well as details about the transaction itself.
 * 
 * @see TransactionInfo
 * @see AccountInfo
 */
@Getter
@Setter
public class TransferResponse {

    /**
     * The account from which the money was transferred.
     */
    private AccountInfo sourceAccount;
    /**
     * The account to which the money was transferred.
     */
    private AccountInfo targetAccount;
    /**
     * The transaction information related to the transfer.
     */
    private TransactionInfo transactionInfo;

    /**
     * Constructs a new TransferResponse encapsulating transfer details by converting provided
     * domain objects into their corresponding DTO representations.
     *
     * <p>The source and target account instances are transformed into AccountInfo objects using the
     * AccountInfoAdapter, and the transaction instance is converted into a TransactionInfo using the
     * TransactionInfoAdapter.</p>
     *
     * @param sourceAccount the source account for the transfer
     * @param targetAccount the target account for the transfer
     * @param transactionInfo the transaction details associated with the transfer
     */
    public TransferResponse(Account sourceAccount, Account targetAccount, Transaction transactionInfo) {
        this.sourceAccount = AccountInfoAdapter.fromAccount(sourceAccount);
        this.targetAccount = AccountInfoAdapter.fromAccount(targetAccount);
        this.transactionInfo = TransactionInfoAdapter.fromTransaction(transactionInfo);
    }
}
