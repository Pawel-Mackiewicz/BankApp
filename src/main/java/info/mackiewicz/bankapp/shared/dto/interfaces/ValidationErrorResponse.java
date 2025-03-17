package info.mackiewicz.bankapp.shared.dto.interfaces;

import java.util.List;

import info.mackiewicz.bankapp.shared.dto.ValidationError;

public interface ValidationErrorResponse extends ApiErrorResponse{
    List<ValidationError> getErrors();

}
