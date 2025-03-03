package info.mackiewicz.bankapp.account.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import info.mackiewicz.bankapp.account.model.dto.AccountOwnerDTO;
import info.mackiewicz.bankapp.account.util.IbanGenerator;
import info.mackiewicz.bankapp.user.model.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    String iban;

    @Column(name = "user_account_number", nullable = false)
    Integer userAccountNumber;
    
    @Column(name = "creation_date")
    LocalDateTime creationDate;

    @Setter
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "owner_id")
    User owner;

    BigDecimal balance;

    protected Account() {
    }

    @JsonProperty("owner")
    public AccountOwnerDTO getOwnerDTO() {
        return owner != null ? new AccountOwnerDTO(owner) : null;
    }

    @JsonProperty("owner_id")
    public Integer getOwnerId() {
        return owner != null ? owner.getId() : null;
    }

    public String getFormattedIban() {
        return IbanGenerator.formatIban(this.iban);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Account account = (Account) o;
        return Objects.equals(id, account.id) && balance.equals(account.balance);
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(id);
        result = (31 * result) + (balance != null ? balance.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("Account #%d [balance = %.2f]", userAccountNumber, balance);
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
}
