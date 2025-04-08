package info.mackiewicz.bankapp.user.model.interfaces;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserDetailsWithId extends UserDetails {

    /**
 * Returns the unique identifier associated with the user.
 *
 * <p>This identifier complements the standard user details by providing an additional key to uniquely distinguish
 * a user within the application.
 *
 * @return the user's unique identifier
 */
Integer getId();

}
