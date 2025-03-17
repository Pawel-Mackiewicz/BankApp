package info.mackiewicz.bankapp.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ValidationError {
    private String field;         
    private String message;       
    private String rejectedValue; 
}
