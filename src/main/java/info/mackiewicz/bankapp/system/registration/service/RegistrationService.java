package info.mackiewicz.bankapp.system.registration.service;

import info.mackiewicz.bankapp.system.registration.dto.RegistrationRequest;
import info.mackiewicz.bankapp.user.model.User;

public interface RegistrationService {

    User registerUser(RegistrationRequest request);
}
