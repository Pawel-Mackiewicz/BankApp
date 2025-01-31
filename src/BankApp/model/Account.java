package BankApp.model;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
    public static final Account BANK;
    private static int idCounter;
    private int id;
    private int balance;
    private final ReentrantLock lock = new ReentrantLock();

    static {
        idCounter = 0;
        BANK = new Account(999_999_999);
    }

    public Account(int balance) {
        this.balance = balance;
        this.id = idCounter++;
    }

    public void deposit(int amount) {
        balance += amount;
    }

    public void withdraw(int amount) {
        balance -= amount;
    }
    public boolean canWithdraw(int amount) {
        if (balance >= amount) {
            return true;
        }
        return false;
    }

    public int getBalance() {
        return balance;
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() throws IllegalMonitorStateException {
        lock.unlock();
    }

    public int getId() {
        return id;
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
    public String toString() {
        return String.format("Account #%d [balance = %d]", id, balance);
    }

}