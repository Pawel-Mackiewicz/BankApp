package info.mackiewicz.bankapp.validation;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class RequestValidator {
    
    public ResponseEntity<?> validateRequest(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ValidationErrorResponse.fromBindingResult(bindingResult);
        }
        return null;
    }
    
    public boolean hasErrors(BindingResult bindingResult) {
        return bindingResult.hasErrors();
    }
}