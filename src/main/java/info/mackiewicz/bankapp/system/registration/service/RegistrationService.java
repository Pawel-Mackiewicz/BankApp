package info.mackiewicz.bankapp.system.registration.service;

import info.mackiewicz.bankapp.system.registration.dto.RegistrationRequest;
import info.mackiewicz.bankapp.system.registration.dto.RegistrationResponse;
/**
     * Registers a new user based on the provided registration data.
     *
     * @param request DTO containing all necessary user registration information
     * @return DTO containing information about the newly registered user
     * @throws DuplicatedUserException if a user with duplicate unique identifiers exists
     * @throws UserBaseException if the registration request contains invalid data
     */
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
