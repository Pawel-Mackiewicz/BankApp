package BankApp.model;

import BankApp.strategy.*;

public class Transaction {
    private TransactionStrategy strategy;
    private TransactionType type;
    private static int idCounter;
    private int id;
    private Account from;
    private Account to;
    private int amount;

    static {
        idCounter = 1;
    }

    //TRANSFER
    public Transaction(Account from, Account to, int amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.id = idCounter++;
        this.type = TransactionType.TRANSFER;
        this.strategy = new TransferTransaction();
    }

    // DEPOSIT | WITHDRAWAL | FEE
    public Transaction(TransactionType type, Account account, int amount) {
        this.type = type;
        this.amount = amount;
        this.id = idCounter++;

        switch (type) {
            case WITHDRAWAL -> {
                this.strategy = new WithdrawalTransaction();
                this.from = account;
            }
            case DEPOSIT -> {
                this.strategy = new DepositTransaction();
                this.to = account;
            }
            case FEE -> {
                this.strategy = new FeeTransaction();
                this.from = account;
                this.to = Account.BANK;
            }
            default ->
                    throw new IllegalArgumentException("You chosen wrong transaction type for this constructor: " + type);
        }
    }

    public boolean isTransactionPossible() {
        return type.equals(TransactionType.DEPOSIT) || from.canWithdraw(amount);
    }

    public void execute() {
        this.strategy.execute(this);
    }

    public int getId() {
        return id;
    }

    public Account getFrom() {
        return from;
    }

    public Account getTo() {
        return to;
    }

    public int getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }
}
