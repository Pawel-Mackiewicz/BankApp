package info.mackiewicz.bankapp.shared.dto;

import lombok.Value;

@Value
public class ValidationError {
    private String field;         
    private String message;       
    private String rejectedValue; 
}
