package info.mackiewicz.bankapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangeAliasRequest {
    
    @NotBlank(message = "Alias jest wymagany")
    @Size(min = 3, max = 50, message = "Alias musi mieć od 3 do 50 znaków")
    private String newAlias;

    // Getter i setter
    public String getNewAlias() {
        return newAlias;
    }

    public void setNewAlias(String newAlias) {
        this.newAlias = newAlias;
    }
}