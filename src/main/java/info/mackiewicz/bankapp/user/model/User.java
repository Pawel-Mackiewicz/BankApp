package info.mackiewicz.bankapp.user.model;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import info.mackiewicz.bankapp.account.model.Account;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String PESEL;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    @JsonIgnore
    private String password;

    @Column(nullable = false)
    private boolean expired;

    @Column(nullable = false)
    private boolean credentialsExpired;

    @Column(nullable = false)
    private boolean locked;

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "account_counter")
    private Integer accountCounter;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Account> accounts;

    public User() {
        expired = false;
        credentialsExpired = false;
        locked = false;
        enabled = true;
        roles = new HashSet<>();
        addDefaultRole();
        accounts = new HashSet<>();
        accountCounter = 0;
    }

    public synchronized Integer getNextAccountNumber() {
        return ++accountCounter;
    }

    private void addDefaultRole() {
        roles.add("ROLE_USER");
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(PESEL, user.PESEL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, PESEL);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
        return !expired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !expired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return "User(id=" + id +
                ", PESEL=" + PESEL +
                ", firstname=" + firstname +
                ", lastname=" + lastname +
                ", username=" + username +
                ", email=" + email +
                ")";
    }

    public String getFullName() {
        String first = firstname == null ? "" : firstname.trim();
        String last = lastname == null ? "" : lastname.trim();
        return first + " " + last;
    }
}
