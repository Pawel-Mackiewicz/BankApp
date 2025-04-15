package info.mackiewicz.bankapp.system.registration.service;

import info.mackiewicz.bankapp.system.registration.dto.DemoRegistrationRequest;
import info.mackiewicz.bankapp.system.registration.dto.RegistrationRequest;
import info.mackiewicz.bankapp.system.registration.dto.RegistrationResponse;
import info.mackiewicz.bankapp.system.registration.dto.demo.RegistrationRequestFactory;
import info.mackiewicz.bankapp.system.registration.exception.DemoRegistrationException;
import info.mackiewicz.bankapp.user.exception.DuplicatedEmailException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service responsible for handling the registration of demo users.
 * Encapsulates the logic for creating a demo registration request and interacting
 * with the user registration handling system.
 * <p>
 * This service performs the following:
 * <br>- Creates a demo user registration request using the {@link RegistrationRequestFactory}.
 * <br>- Invokes the {@link RegistrationService#registerUser(RegistrationRequest)} method to
 * register the user in the system.
 * <br>- Handles exceptions that may occur during the registration process, including duplicate
 * email errors and internal application errors.
 * <p>
 * Dependencies:
 * <br>- {@link RegistrationService}: Handles the actual registration process.
 * <br>- {@link RegistrationRequestFactory}: Creates a demo registration request with default
 * placeholder values.
 */
@Service
@RequiredArgsConstructor
public class DemoRegistrationService {

    private final RegistrationService registrationService;
    private final RegistrationRequestFactory requestFactory;

    public RegistrationResponse registerDemoUser(DemoRegistrationRequest demoRequest) {
        RegistrationRequest request = requestFactory.createDemoRegistrationRequest(demoRequest.getEmail(), demoRequest.getPassword());
        try {
            return registrationService.registerUser(request);
        } catch (Exception e) {
            // Handle email duplication specifically as it's a common user error
            // All other exceptions are wrapped as internal system errors
            if (e instanceof DuplicatedEmailException) {
                throw e;
            }
            throw new DemoRegistrationException("Registration failed", e);
        }
    }
}
