package info.mackiewicz.bankapp.user.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * Base class for all user types in the system that provides core user functionality
 * and security-related features through Spring Security's UserDetails interface.
 *
 * <p>This abstract class implements common user attributes and security-related fields
 * required for authentication and authorization in the banking application. It includes:
 * <ul>
 *   <li>Basic user identification (ID, username)
 *   <li>Security status flags (account expiration, credentials expiration, etc.)
 *   <li>Role-based authorization support
 * </ul>
 *
 * <p>All user-specific classes in the system should extend this base class to inherit
 * the security infrastructure and common user management functionality.
 *
 * @see org.springframework.security.core.userdetails.UserDetails
 * @see org.springframework.security.core.GrantedAuthority
 */
@Getter
@Setter
@MappedSuperclass
public abstract class BaseUser implements UserDetails {
    /**
     * Unique identifier for the user entity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    /**
     * Unique username used for authentication.
     * This field cannot be null and must be unique across all users.
     */
    @Column(unique = true, nullable = false)
    protected String username;

    /**
     * Encrypted password used for authentication.
     * This field is excluded from JSON serialization for security purposes.
     */
    @JsonIgnore
    protected String password;

    /**
     * Flag indicating whether the user's account has expired.
     * An expired account cannot be used for authentication.
     */
    @Column(nullable = false)
    protected boolean expired;

    /**
     * Flag indicating whether the user's credentials (password) have expired.
     * Expired credentials require the user to change their password.
     */
    @Column(nullable = false)
    protected boolean credentialsExpired;

    /**
     * Flag indicating whether the user's account is locked.
     * A locked account cannot be used for authentication until it is unlocked.
     */
    @Column(nullable = false)
    protected boolean locked;

    /**
     * Flag indicating whether the user's account is enabled.
     * Disabled accounts cannot be used for authentication.
     */
    @Column(nullable = false)
    protected boolean enabled;

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
     * Protected constructor for creating a new base user.
     * Initializes the user with default security settings:
     * - Not expired
     * - Credentials not expired
     * - Not locked
     * - Enabled
     * - Empty roles set
     */
    protected BaseUser() {
        this.expired = false;
        this.credentialsExpired = false;
        this.locked = false;
        this.enabled = true;
        this.roles = new HashSet<>();
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

    /**
     * Sets the user's password.
     * This method is annotated with @JsonProperty to allow password setting during
     * deserialization while keeping the password field itself ignored in serialization.
     *
     * @param password the password to set for the user
     */
    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Checks if the account has not expired.
     * This is part of Spring Security's UserDetails contract.
     *
     * @return true if the account is not expired, false otherwise
     */
    @Override
    public boolean isAccountNonExpired() {
        return !expired;
    }

    /**
     * Checks if the account is not locked.
     * This is part of Spring Security's UserDetails contract.
     *
     * @return true if the account is not locked, false otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    /**
     * Checks if the credentials have not expired.
     * This is part of Spring Security's UserDetails contract.
     *
     * @return true if the credentials are not expired, false otherwise
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return !credentialsExpired;
    }

    /**
     * Checks if the account is enabled.
     * This is part of Spring Security's UserDetails contract.
     *
     * @return true if the account is enabled, false otherwise
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();
}