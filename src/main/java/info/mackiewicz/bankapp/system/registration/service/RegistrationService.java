package info.mackiewicz.bankapp.system.registration.service;

import info.mackiewicz.bankapp.system.registration.dto.RegistrationRequest;
import info.mackiewicz.bankapp.system.registration.dto.RegistrationResponse;

public interface RegistrationService {

    RegistrationResponse registerUser(RegistrationRequest request);
}
