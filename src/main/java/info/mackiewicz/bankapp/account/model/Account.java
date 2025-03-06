package info.mackiewicz.bankapp.account.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import org.iban4j.Iban;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import info.mackiewicz.bankapp.account.model.dto.AccountOwnerDTO;
import info.mackiewicz.bankapp.account.service.AccountServiceAccessManager;
import info.mackiewicz.bankapp.account.util.IbanConverter;
import info.mackiewicz.bankapp.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

/**
 * Entity representing a bank account in the system.
 */
@Entity
@Table(name = "accounts")
public class Account {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    @Convert(converter = IbanConverter.class)
    @Column(unique = true, nullable = false)
    private Iban iban;

    @Getter
    @Column(name = "user_account_number", nullable = false)
    private Integer userAccountNumber;

    @Getter
    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Getter
    private BigDecimal balance;

    /**
     * Default constructor for JPA.
     */
    Account() {
    }

    /**
     * Creates a new account with specified owner, account number and IBAN.
     * This constructor is package-private to enforce creation through
     * AccountFactory.
     *
     * @param owner             The user who owns this account
     * @param userAccountNumber The user-specific account number
     * @param iban              The International Bank Account Number
     */
    Account(User owner, int userAccountNumber, Iban iban) {
        this.creationDate = LocalDateTime.now();
        this.balance = BigDecimal.ZERO;
        this.owner = owner;
        this.userAccountNumber = userAccountNumber;
        this.iban = iban;
    }

    public static AccountFactory factory() {
        return new AccountFactory();
    }

    public void setBalance(BigDecimal newBalance) {
        AccountServiceAccessManager.checkServiceAccess();
        this.balance = newBalance;
    }

    /**
     * Returns the IBAN in a formatted, human-readable form.
     *
     * @return The formatted IBAN string
     */
    public String getFormattedIban() {
        return iban.toFormattedString();
    }

    /**
     * Returns DTO with account owner's ID and full name.
     *
     * @return The owner's DTO
     */
    @JsonGetter("owner")
    public AccountOwnerDTO getOwner() {
        return new AccountOwnerDTO(owner);
    }


    /**
     * Returns a string representation of this account.
     *
     * @return A string containing the account number and balance
     */
    @Override
    public String toString() {
        return String.format("Account IBAN #%s [balance = %.2f]", iban.toFormattedString(), balance);
    }

    /**
     * Compares this account to another object for equality.
     * Two accounts are considered equal if they have the same ID or the same IBAN.
     *
     * @param o The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Account account = (Account) o;
        return iban.equals(account.iban);
    }

    /**
     * Returns a hash code value for this account.
     * The hash code is based on the account ID and IBAN.
     *
     * @return A hash code value for this account
     */
    @Override
    public int hashCode() {
        return Objects.hash(iban);
    }
}
