package info.mackiewicz.bankapp.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import info.mackiewicz.bankapp.model.Account;
import info.mackiewicz.bankapp.model.Transaction;
import lombok.Data;

@Data
public class DashboardDTO {
    private Integer userId;
    private List<Account> accounts = new ArrayList<>();
    private List<Transaction> recentTransactions = new ArrayList<>();
    private BigDecimal totalBalance = BigDecimal.ZERO;
    private BigDecimal totalIncoming = BigDecimal.ZERO;
    private BigDecimal totalOutgoing = BigDecimal.ZERO;
    private String accountName = "";
    private String accountNumber = "";
    private BigDecimal balance = BigDecimal.ZERO;
    private String currency = "PLN";

    public boolean isRecipient(Transaction transaction) {
        return switch (transaction.getType()) {
            case TRANSFER -> userId.equals(transaction.getDestinationAccount().getOwner().getId());
            case WITHDRAWAL, FEE -> false;
            default -> true;
        };
    }

    public String getOtherPartyName(Transaction transaction) {
        return switch (transaction.getType()) {
            case TRANSFER -> isRecipient(transaction)
                ? transaction.getDestinationAccount().getOwner().getFullName()
                : transaction.getSourceAccount().getOwner().getFullName();
            case DEPOSIT -> "Deposit";
            case WITHDRAWAL -> "Withdrawal";
            default -> "Fee";
        };
    }
}
