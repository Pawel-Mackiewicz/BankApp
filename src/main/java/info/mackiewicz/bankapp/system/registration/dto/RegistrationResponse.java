package info.mackiewicz.bankapp.system.registration.dto;

import info.mackiewicz.bankapp.user.model.vo.EmailAddress;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record RegistrationResponse(
        @Schema(example = "John")
        String firstname,
        @Schema(example = "Smith")
        String lastname,
        @Schema(example = "John.Smith@example.com")
        EmailAddress email,
        @Schema(example = "john.smith12345")
        String username) {
}
