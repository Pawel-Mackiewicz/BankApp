package info.mackiewicz.bankapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReentrantLock;
@NoArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {

    //TODO: private static Double differenceInAccounts;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Integer id;

    @Getter
    private BigDecimal balance;

    @Getter
    @Setter
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "owner_id")
    private Person owner;

    @Transient
    private final ReentrantLock lock = new ReentrantLock();

    public Account(Person owner) {
        this.owner = owner;
        balance = BigDecimal.ZERO;
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() throws IllegalMonitorStateException {
        lock.unlock();
    }

    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        balance = balance.subtract(amount);
    }

    public boolean canWithdraw(BigDecimal amount) {
        return (balance.compareTo(amount) >= 0);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;
        return id == account.id && balance.equals(account.balance);
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(id);
        result = 31 * result + balance.hashCode();
        return result;
    }

    @Override
    public String toString() { return String.format("Account #%d [balance = %.2f]", id, balance);
    }
}
