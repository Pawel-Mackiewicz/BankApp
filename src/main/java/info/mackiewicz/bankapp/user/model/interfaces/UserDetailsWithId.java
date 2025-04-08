package info.mackiewicz.bankapp.user.model.interfaces;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserDetailsWithId extends UserDetails {

    /**
 * Returns the unique identifier for the user.
 *
 * @return the user's identifier as an Integer
 */
Integer getId();

}
