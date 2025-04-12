package info.mackiewicz.bankapp.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.user.model.interfaces.AccountOwner;
import info.mackiewicz.bankapp.user.model.interfaces.PersonalInfo;
import info.mackiewicz.bankapp.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.user.model.vo.Pesel;
import info.mackiewicz.bankapp.user.model.vo.PhoneNumber;
import info.mackiewicz.bankapp.user.service.UserService;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a regular bank user.
 * Contains personal information and account relationships.
 * For new user creation use static factory method {@link User#builder()}.
 * 
 * @see UserService UserService for business logic.
 */
@Entity
@Data
@Table(name = "users")
public class User extends BaseUser implements PersonalInfo, AccountOwner {

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "pesel", unique = true, nullable = false))
    private Pesel pesel;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email", unique = true, nullable = false))
    private EmailAddress email;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "phone_number", unique = true, nullable = false))
    private PhoneNumber phoneNumber;

    
    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Account> accounts;
    
    @Column(name = "account_counter")
    private Integer accountCounter;

        /**
     * Collection of security roles assigned to the user.
     * These roles are used for authorization and access control.
     * The roles are eagerly fetched to ensure they are always available
     * for security checks.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @OnDelete(action = OnDeleteAction.CASCADE)
    protected Set<String> roles;
   
    
    /**
     * Default constructor for JPA and testing
     * For new user creation use static factory method {@link User#builder()}.
     */
    public User() {
        super(); // Initialize base fields
        initalizeDefaultRoles();
        accounts = new HashSet<>();
        accountCounter = 0;
    }
    private void initalizeDefaultRoles() {
        roles = new HashSet<>();
        roles.add("ROLE_USER");
    }
    
    User(String password, Pesel pesel, String firstname, String lastname, LocalDate dateOfBirth,
            EmailAddress email, PhoneNumber phoneNumber) {
        this();
        this.password = password;
        this.pesel = pesel;
        this.firstname = firstname;
        this.lastname = lastname;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Creates a new user builder.
     * 
     * @return
     *         Builder class for creating User instances with a step interface.
     *         This class simplifies the process of creating new users by providing
     *         a clean and readable way to set various user attributes.
     *        <p>
     *         The builder supports setting both primitive values and value objects
     *         for user attributes, ensuring proper encapsulation and validation.
     *
     * @see UserBuilder UserBuilder for user creation
     */
    public static UserBuilder.FirstnameStep builder() {
        return UserBuilder.builder();
    }
    
    /**
     * Creates a test user builder for creating User objects in test environments.
     * This builder provides default values for all required fields, making it easier
     * to create valid User objects for testing purposes without specifying all fields.
     * <p>
     * Unlike the standard {@link #builder()} method, this test builder initializes all
     * required fields with sensible test values and provides a fluent API to override
     * only the fields that need to be customized for a specific test case.
     * <p>
     * Example usage:
     * <pre>
     * // Create a user with all default test values
     * User defaultTestUser = User.testBuilder().build();
     * 
     * // Create a user with customized fields
     * User customTestUser = User.testBuilder()
     *     .withFirstname("John")
     *     .withLastname("Doe")
     *     .withEmail("john.doe@example.com")
     *     .build();
     * </pre>
     * 
     * @return A TestUserBuilder instance with pre-initialized default values
     * @see TestUserBuilder For more details on available customization options
     */
    public static TestUserBuilder testBuilder() {
        return TestUserBuilder.aUser();
    }

        /**
     * Returns the collection of granted authorities based on the user's roles.
     * Each role string is converted to a SimpleGrantedAuthority object.
     *
     * @return Collection of GrantedAuthority objects representing the user's roles
     * @see org.springframework.security.core.authority.SimpleGrantedAuthority
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    public synchronized Integer getNextAccountNumber() {
        return ++accountCounter;
    }

    public String getFullName() {
        String first = firstname == null ? "" : firstname.trim();
        String last = lastname == null ? "" : lastname.trim();
        return first + " " + last;
    }

    public void setPesel(Pesel pesel) {
        this.pesel = pesel;
    }

    public void setEmail(EmailAddress email) {
        this.email = email;
    }
    
    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
        return true;
        if (o == null || getClass() != o.getClass())
        return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(pesel, user.pesel);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, pesel);
    }

    @Override
    public String toString() {
        return "User(id=" + id +
        ", pesel=" + pesel +
        ", firstname=" + firstname +
        ", lastname=" + lastname +
        ", username=" + username +
        ", email=" + email +
        ")";
    }
}