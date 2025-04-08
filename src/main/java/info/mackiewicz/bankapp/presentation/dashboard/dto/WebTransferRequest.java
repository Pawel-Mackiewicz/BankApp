package info.mackiewicz.bankapp.presentation.dashboard.dto;

import info.mackiewicz.bankapp.transaction.model.TransactionType;

public interface WebTransferRequest {

    /**
 * Retrieves the source IBAN for the web transfer request.
 *
 * @return the source IBAN as a String
 */
String getSourceIban();

    String getRecipientIban();

    String getAmount();

    String getTitle();

    TransactionType getTransactionType();

    void setSourceIban(String sourceIban);

    void setRecipientIban(String recipientIban);

    void setAmount(String amount);

    void setTitle(String title);

    void setTransactionType(TransactionType transactionType);
}
