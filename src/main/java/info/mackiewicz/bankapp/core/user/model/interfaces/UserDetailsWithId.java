package info.mackiewicz.bankapp.core.user.model.interfaces;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserDetailsWithId extends UserDetails {

    Integer getId();

}
