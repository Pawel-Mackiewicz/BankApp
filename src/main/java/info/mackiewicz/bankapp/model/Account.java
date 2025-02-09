package info.mackiewicz.bankapp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
@NoArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Integer id;

    @Getter
    private BigDecimal balance;

    @Getter
    @Setter
    @JsonBackReference
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Transient
    private final ReentrantLock lock = new ReentrantLock();

    public Account(User owner) {
        this.owner = owner;
        balance = BigDecimal.ZERO;
    }

    @JsonProperty("owner_id")
    public Integer getOwnerId() {
        return owner != null ? owner.getId() : null;
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

    //There is two checks, one after another in every transaction. leave it?
    public boolean canWithdraw(BigDecimal amount) {

        return (balance.compareTo(amount) >= 0);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;
        return Objects.equals(id, account.id) && balance.equals(account.balance);
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
