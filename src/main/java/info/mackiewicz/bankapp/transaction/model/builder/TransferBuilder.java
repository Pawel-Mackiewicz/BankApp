package info.mackiewicz.bankapp.transaction.model.builder;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.system.banking.operations.service.helpers.IbanAnalysisService;
import info.mackiewicz.bankapp.transaction.exception.TransactionBuildingException;
import info.mackiewicz.bankapp.transaction.exception.TransactionDestinationAccountNotSpecifiedException;
import info.mackiewicz.bankapp.transaction.exception.TransactionSourceAccountNotSpecifiedException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.iban4j.Iban;

@RequiredArgsConstructor
public class TransferBuilder extends AbstractTransactionBuilder<TransferBuilder> {
    private Account sourceAccount;
    private Account destinationAccount;

    public TransferBuilder from(Account account) {
        this.sourceAccount = account;
        return this;
    }

    public TransferBuilder to(Account account) {
        this.destinationAccount = account;
        return this;
    }

    @Override
    protected void validate() {
        validateAmount();
        validateType();
        if (sourceAccount == null) {
            throw new TransactionSourceAccountNotSpecifiedException();
        }
        if (destinationAccount == null) {
            throw new TransactionDestinationAccountNotSpecifiedException();
        }
    }

    @Override
    public Transaction build() {
        try {
            resolveTransactionType();
            validate();
            Transaction transaction = createBaseTransaction();
            transaction.setSourceAccount(sourceAccount);
            transaction.setDestinationAccount(destinationAccount);
            return transaction;
        } catch (Exception e) {
            throw new TransactionBuildingException("Error building transaction for account ID: " +
                    (sourceAccount != null ? sourceAccount.getId() : "unknown"), e);
        }
    }

    private void resolveTransactionType() {
        Iban sourceIban = sourceAccount.getIban();
        Iban destinationIban = destinationAccount.getIban();
        this.type = IbanAnalysisService.resolveTransferType(sourceIban, destinationIban);
    }
}