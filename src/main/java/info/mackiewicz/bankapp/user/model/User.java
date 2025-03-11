package info.mackiewicz.bankapp.user.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import info.mackiewicz.bankapp.user.service.UserService;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.user.model.vo.Email;
import info.mackiewicz.bankapp.user.model.vo.Pesel;
import info.mackiewicz.bankapp.user.model.vo.PhoneNumber;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a regular bank user.
 * Contains personal information and account relationships.
 * For new user creation use static factory method {@link User#builder()}.
 * @see UserService UserService for business logic.
 */
@Entity
@Getter
@Setter
@Table(name = "users")
public class User extends BaseUser {

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "pesel", unique = true, nullable = false))
    private Pesel PESEL;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email", unique = true, nullable = false))
    private Email email;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "phone_number", unique = true, nullable = false))
    private PhoneNumber phoneNumber;

    @Column(name = "account_counter")
    private Integer accountCounter;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Account> accounts;

    /** 
     * Default constructor for JPA.
     * For new user creation use static factory method {@link User#builder()}.
     */
    public User() {
        super(); // Initialize base fields
        addDefaultRole();
        accounts = new HashSet<>();
        accountCounter = 0;
    }

    User(String username, String password, Pesel pesel, String firstname, String lastname, LocalDate dateOfBirth, Email email, PhoneNumber phoneNumber) {
        this();
        this.username = username;
        this.password = password;   
        this.PESEL = pesel;
        this.firstname = firstname;
        this.lastname = lastname;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    private void addDefaultRole() {
        roles.add("ROLE_USER");
    }

    public synchronized Integer getNextAccountNumber() {
        return ++accountCounter;
    }

    public String getFullName() {
        String first = firstname == null ? "" : firstname.trim();
        String last = lastname == null ? "" : lastname.trim();
        return first + " " + last;
    }

    @Override
    public String toString() {
        return "User(id=" + id +
                ", pesel=" + PESEL +
                ", firstname=" + firstname +
                ", lastname=" + lastname +
                ", username=" + username +
                ", email=" + email +
                ")";
    }

    
    public void setPESEL(Pesel pesel) {
        this.PESEL = pesel;
    }
    
    public void setEmail(Email email) {
        this.email = email;
    }
    
    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    @Deprecated
    public void setPESEL(String pesel) {
        this.PESEL = new Pesel(pesel);
    }
    @Deprecated
    public void setEmail(String email) {
        this.email = new Email(email);
    }
    @Deprecated
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = new PhoneNumber(phoneNumber);
    }
}