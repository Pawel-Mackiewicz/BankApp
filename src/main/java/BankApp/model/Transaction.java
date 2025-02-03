package BankApp.model;

import BankApp.strategy.TransactionStrategy;
import lombok.Getter;

public class Transaction {
    private static int idCounter = 1;
    @Getter
    private final int id;
    @Getter
    private final Account from;
    @Getter
    private final Account to;
    @Getter
    private final double amount;
    @Getter
    private final TransactionType type;
    private final TransactionStrategy strategy;

    public Transaction(Account account, double amount, TransactionType type, TransactionStrategy strategy) {
        this(getFromAccount(account, type), getToAccount(account, type), amount, type, strategy);
    }

    public Transaction(Account from, Account to, double amount, TransactionType type, TransactionStrategy strategy) {
        this.id = idCounter++;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.type = type;
        this.strategy = strategy;
    }

    private static Account getFromAccount(Account account, TransactionType type) {
        return type == TransactionType.DEPOSIT ? null : account;
    }

    private static Account getToAccount(Account account, TransactionType type) {
        return type == TransactionType.DEPOSIT ? account : null;
    }

    public boolean isTransactionPossible() {
        if (type == TransactionType.DEPOSIT) {
            return to != null;
        }
        return from != null && from.canWithdraw(amount);
    }

    public boolean execute() {
        return strategy.execute(this);
    }

}
