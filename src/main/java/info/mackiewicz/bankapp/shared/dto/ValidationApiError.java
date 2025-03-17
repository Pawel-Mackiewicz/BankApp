package info.mackiewicz.bankapp.shared.dto;

import java.util.List;


import info.mackiewicz.bankapp.shared.dto.interfaces.ValidationErrorResponse;
import info.mackiewicz.bankapp.shared.exception.handlers.ErrorCode;
import lombok.Getter;

@Getter
public class ValidationApiError extends BaseApiError implements ValidationErrorResponse {

    private final List<ValidationError> errors;

    public ValidationApiError(ErrorCode errorCode, String path, List<ValidationError> errors) {
        super(errorCode, path);
        this.errors = errors;
    }
}
