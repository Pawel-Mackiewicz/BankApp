package info.mackiewicz.bankapp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Integer id;

    @Getter
    @Column(name = "user_account_number")
    private Integer userAccountNumber;

    @Getter
    @Setter
    private String iban;

    @Getter
    private BigDecimal balance;

    @Getter
    @Column(name = "creation_date")
    private LocalDateTime creationDate;

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
        this.balance = BigDecimal.ZERO;
        this.creationDate = LocalDateTime.now();
        this.userAccountNumber = owner.getNextAccountNumber();  
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

    // There is two checks, one after another in every transaction. leave it?
    public boolean canWithdraw(BigDecimal amount) {

        return (balance.compareTo(amount) >= 0);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;

        Account account = (Account) o;
        return Objects.equals(id, account.id) && balance.equals(account.balance);
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(id);
        result = (31 * result) + (balance != null ? balance.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("Account #%d [balance = %.2f]", userAccountNumber, balance);
    }
}
