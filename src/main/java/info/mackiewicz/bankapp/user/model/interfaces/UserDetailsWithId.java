package info.mackiewicz.bankapp.user.model.interfaces;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserDetailsWithId extends UserDetails {

    Integer getId();

}
