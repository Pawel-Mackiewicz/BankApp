package info.mackiewicz.bankapp.system.registration.service;

import info.mackiewicz.bankapp.system.registration.dto.RegistrationRequest;
import info.mackiewicz.bankapp.system.registration.dto.RegistrationResponse;

/**
 * Defines a service responsible for handling user registration processes.
 * This service encapsulates all the necessary logic to register a new user, 
 * including user account creation, granting of a welcome bonus, and sending notifications.
 *<p>
 * The implementation of this interface ensures the following:
 * <br>- Validating and processing the user registration request.
 * <br>- Creating a new user entity based on the provided registration data.
 * <br>- Generating a new user account and associating it with the registered user.
 * <br>- Granting a predefined welcome bonus to the newly created account.
 * <br>- Sending a welcome notification (e.g., email) to the registered user.
 *<p>
 * The method exposed in this service facilitates integration with external systems or components
 * that require user registration capabilities.
 */
public interface RegistrationService {
    
    /**
     * Registers a new user based on the provided registration data.
     *
     * @param request DTO containing all necessary user registration information
     * @return DTO containing information about the newly registered user
     */
    RegistrationResponse registerUser(RegistrationRequest request);
}
     *
     * @param request DTO containing all necessary user registration information
     * @return DTO containing information about the newly registered user
     * @throws DuplicatedUserException if a user with duplicate unique identifiers exists
     * @throws UserBaseException if the registration request contains invalid data
     */
    RegistrationResponse registerUser(RegistrationRequest request);
}
