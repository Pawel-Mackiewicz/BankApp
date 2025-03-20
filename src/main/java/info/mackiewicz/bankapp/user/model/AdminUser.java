package info.mackiewicz.bankapp.user.model;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Data;


/**
 * Represents an administrative user with elevated privileges.
 * Inherits common user functionality from BaseUser.
 */
@Data
@Entity
@Table(name = "admins")
public class AdminUser extends BaseUser {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;
    
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "admin_roles", joinColumns = @JoinColumn(name = "admin_id"))
    @Column(name = "role")
    private Set<String> roles;
    
    @Column(nullable = false)
    private boolean expired;
    
    @Column(nullable = false)
    private boolean credentialsExpired;
    
    @Column(nullable = false)
    private boolean locked;
    
    @Column(nullable = false)
    private boolean enabled;
    
    public AdminUser() {
        super();
        addDefaultRole();
    }
    public AdminUser(String username, String password) {
        super();
    this.username = username;
        this.password = password;
        addDefaultRole();
    }

    private void addDefaultRole() {
        roles = Set.of("ROLE_ADMIN");
        }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Admin accounts never expire
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Admin accounts are never locked
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Admin credentials never expire
    }

    @Override
    public boolean isEnabled() {
        return true; // Admin accounts are always enabled
    }

    @Override
    public String toString() {
        return "AdminUser(id=" + id + 
               ", username=" + username + 
               ")";
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof AdminUser)) {
            return false;
        }
        AdminUser adminUser = (AdminUser) o;
        return id != null && id.equals(adminUser.id);
    }

    @Override
    public int hashCode() {
        int result = 31 * id.hashCode() + 31 * username.hashCode();
        return result;
    }
}
