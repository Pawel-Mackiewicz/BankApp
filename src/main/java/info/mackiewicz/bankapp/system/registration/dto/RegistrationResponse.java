package info.mackiewicz.bankapp.system.registration.dto;

import info.mackiewicz.bankapp.user.model.vo.EmailAddress;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record RegistrationResponse(
        String firstname,
        String lastname,
        EmailAddress email,
        String username) {
}
