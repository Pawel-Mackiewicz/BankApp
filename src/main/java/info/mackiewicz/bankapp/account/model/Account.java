package info.mackiewicz.bankapp.account.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import info.mackiewicz.bankapp.account.model.interfaces.OwnershipInfo;
import info.mackiewicz.bankapp.account.service.interfaces.FinancialOperations;
import info.mackiewicz.bankapp.account.util.IbanGenerator;
import info.mackiewicz.bankapp.shared.exception.InvalidOperationException;
import info.mackiewicz.bankapp.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "accounts")
public class Account implements FinancialOperations, OwnershipInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String iban;

    @Column(name = "user_account_number", nullable = false)
    private Integer userAccountNumber;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    private BigDecimal balance;

    Account() {
        this.creationDate = LocalDateTime.now();
        this.balance = BigDecimal.ZERO;
    }

    Account(User owner, int userAccountNumber, String iban) {
        this();
        this.owner = owner;
        this.userAccountNumber = userAccountNumber;
        this.iban = iban;
    }

    public String getFormattedIban() {
        return IbanGenerator.formatIban(iban);
    }

    // Implementation of OwnershipInfo interface
    public Integer getOwnerId() {
        return owner.getId();
    }

    @Override
    public String getOwnerName() {
        return owner.getFullName();
    }

    // Implementation of FinancialOperations interface
    @Override
    public void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    @Override
    public void withdraw(BigDecimal amount) {
        if (!canWithdraw(amount)) {
            throw new InvalidOperationException("Insufficient funds");
        }
        this.balance = this.balance.subtract(amount);
    }

    @Override
    public boolean canWithdraw(BigDecimal amount) {
        return this.balance.compareTo(amount) >= 0;
    }

    @Override
    public BigDecimal getBalance() {
        return this.balance;
    }

    @Override
    public String toString() {
        return String.format("Account #%d [balance = %.2f]", userAccountNumber, balance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id) ||
                (iban != null && iban.equals(account.iban));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, iban);
    }
}
