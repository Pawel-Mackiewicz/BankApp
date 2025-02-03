package BankApp.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;


@Entity
@Table(name = "accounts")
public class Account {

    public static final Account BANK;
    @Getter
    private static Double differenceInAccounts;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private int id;

    @Getter
    private Double balance;

    private final ReentrantLock lock = new ReentrantLock();

    static {
        differenceInAccounts = 0.0;
        BANK = new Account(999_999_999);
    }

    protected Account() {
        this.balance = 0.0;
    }

    public Account(double balance) {
        this.balance = balance;
    }

    public static synchronized void differenceFromDeposit(double amount) {
        differenceInAccounts -= amount;
    }

    public static synchronized void differenceFromWithdrawal(double amount) {
        differenceInAccounts += amount;
    }

    public void deposit(double amount) {
        balance += amount;
    }

    public void withdraw(double amount) {
        balance -= amount;
    }
    public boolean canWithdraw(double amount) {
        return balance >= amount;
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() throws IllegalMonitorStateException {
        lock.unlock();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id == account.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() { return String.format("Account #%d [balance = %.2f]", id, balance);
    }

}