package info.mackiewicz.bankapp.transaction.model.dto;

import info.mackiewicz.bankapp.account.model.interfaces.AccountInfo;
import info.mackiewicz.bankapp.transaction.model.interfaces.TransactionInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class representing the response of a transfer operation.
 * This class contains information about the source and target accounts,
 * as well as details about the transaction itself.
 * @see TransactionInfo
 * @see AccountInfo
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
}
