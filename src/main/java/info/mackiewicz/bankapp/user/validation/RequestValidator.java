package info.mackiewicz.bankapp.user.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.stream.Collectors;

/**
 * Component responsible for handling validation errors in API requests.
 * Provides consistent error message formatting across the application.
 */
@Component
public class RequestValidator {

    /**
     * Extracts validation error messages from BindingResult
     *
     * @param bindingResult validation result containing errors
     * @return formatted error message string
     */
    public String getValidationErrorMessage(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(", "));
    }
}