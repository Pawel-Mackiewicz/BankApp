package info.mackiewicz.bankapp.user.model.interfaces;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserDetailsWithId extends UserDetails {

    /**
 * Retrieves the unique identifier for the user.
 *
 * @return the user ID as an Integer
 */
Integer getId();

}
