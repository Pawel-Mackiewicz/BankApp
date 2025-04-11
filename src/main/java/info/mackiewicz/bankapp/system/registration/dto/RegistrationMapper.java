package info.mackiewicz.bankapp.system.registration.dto;

import info.mackiewicz.bankapp.user.model.User;

public interface RegistrationMapper {

    /**
     * Converts a RegistrationRequest object to a User object.
     *
     * @param registrationRequest the RegistrationRequest object to convert
     * @return the converted User object
     */
    User toUser(RegistrationRequest registrationRequest);

    /**
     * Converts a User object to a RegistrationResponse object.
     *
     * @param user the User object to convert
     * @return the converted RegistrationResponse object
     */
    RegistrationResponse toResponse(User user);
}
