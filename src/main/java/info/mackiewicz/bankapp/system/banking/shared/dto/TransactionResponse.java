package info.mackiewicz.bankapp.system.banking.shared.dto;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.account.model.adapter.AccountInfoAdapter;
import info.mackiewicz.bankapp.core.account.model.interfaces.AccountInfo;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.adapter.TransactionInfoAdapter;
import info.mackiewicz.bankapp.transaction.model.interfaces.TransactionInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

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
public class TransactionResponse {

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

    public TransactionResponse(Account sourceAccount, Account targetAccount, Transaction transactionInfo) {
        this.sourceAccount = AccountInfoAdapter.fromAccount(sourceAccount);
        this.targetAccount = AccountInfoAdapter.fromAccount(targetAccount);
        this.transactionInfo = TransactionInfoAdapter.fromTransaction(transactionInfo);
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof TransactionResponse that)) return false;

        return Objects.equals(sourceAccount, that.sourceAccount) && Objects.equals(targetAccount, that.targetAccount) && transactionInfo.equals(that.transactionInfo);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(sourceAccount);
        result = 31 * result + Objects.hashCode(targetAccount);
        result = 31 * result + transactionInfo.hashCode();
        return result;
    }
}
