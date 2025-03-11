package info.mackiewicz.bankapp.user.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an administrative user with elevated privileges.
 * Inherits common user functionality from BaseUser.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "admins")
public class AdminUser extends BaseUser {

    public AdminUser(String username, String password) {
        super();
        this.username = username;
        this.password = password;
        addAdminRole();
    }

    private void addAdminRole() {
        roles.add("ROLE_ADMIN");
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
}
