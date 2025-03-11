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
        addDefaultRole();
    }

    private void addDefaultRole() {
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
