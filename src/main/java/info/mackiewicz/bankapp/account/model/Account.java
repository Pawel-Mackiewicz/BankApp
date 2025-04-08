package info.mackiewicz.bankapp.account.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.iban4j.Iban;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import info.mackiewicz.bankapp.account.model.dto.AccountOwnerDTO;
import info.mackiewicz.bankapp.account.model.interfaces.AccountInfo;
import info.mackiewicz.bankapp.account.service.AccountService;
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
 * Each account is associated with a single user and has a unique IBAN.
 * To create a new account, use {@link AccountService#createAccount}.
 * @see AccountService
 */
@Entity
@Table(name = "accounts")
public class Account implements AccountInfo {

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
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User owner;

    @Getter
    private BigDecimal balance;

    /**
     * Default constructor for JPA.
     * This constructor is package-private to prevent direct instantiation.
     * Use {@link AccountService} to create new accounts.
     */
    Account() {
    }

    /**
     * Creates a new account with specified owner, account number and IBAN.
     * This constructor is package-private to enforce creation through
     * {@link AccountService}.
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

    /**
     * Creates a new AccountFactory instance.
     * 
     * @return A new instance of AccountFactory
     */
    public static AccountFactory factory() {
        return new AccountFactory();
    }

    /**
     * Sets the balance of the account.
     * This method is protected by {@link AccountServiceAccessManager} and can only be called
     * from {@link info.mackiewicz.bankapp.account.service.AccountOperationsService}.
     * Any unauthorized access will result in a {@link SecurityException} being thrown.
     *
     * @param newBalance The new balance to set
     * @throws SecurityException if called from an unauthorized context
     */
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
     * Returns the full name of the account owner.
     *
     * @return the account owner's full name
     */
    @Override
    public String getOwnerFullname() {
        return owner.getFullName();
    }
    /**
     * Returns the actual User object that owns this account.
     * This method is for internal use only, for JSON serialization use getOwner()
     * which returns AccountOwnerDTO.
     *
     * @return The User object that owns this account
     */
    @JsonIgnore
    public User getRawOwner() {
        return owner;
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
     * Returns a hash code value for this account based solely on its IBAN.
     *
     * @return the hash code derived from the account's IBAN.
     */
    @Override
    public int hashCode() {
        return Objects.hash(iban);
    }

}
