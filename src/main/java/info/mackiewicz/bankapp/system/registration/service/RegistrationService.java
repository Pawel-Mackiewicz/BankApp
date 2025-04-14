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

    RegistrationResponse registerUser(RegistrationRequest request);
}
