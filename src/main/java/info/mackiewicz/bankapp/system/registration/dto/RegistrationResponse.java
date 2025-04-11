package info.mackiewicz.bankapp.system.registration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record RegistrationResponse(
        @Schema(example = "John")
        String firstname,
        @Schema(example = "Smith")
        String lastname,
        @Schema(example = "John.Smith@example.com")
        String email,
        @Schema(example = "john.smith12345")
        String username) {
}
