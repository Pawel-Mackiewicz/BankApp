package info.mackiewicz.bankapp.core.account.validation.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResponse {
    @NotNull
    private boolean valid;
    @Nullable
    private Boolean found;
    @NotNull
    private String message;

    public static ValidationResponse invalid(String message) {
        return new ValidationResponse(false, null, message);
    }

    public static ValidationResponse valid(String message) {
        return new ValidationResponse(true, null, message);
    }

    public static ValidationResponse notFound(String message) {
        return new ValidationResponse(true, false, message);
    }

    public static ValidationResponse found(String message) {
        return new ValidationResponse(true, true, message);
    }
}
