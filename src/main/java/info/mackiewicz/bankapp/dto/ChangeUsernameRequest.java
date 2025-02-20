package info.mackiewicz.bankapp.dto;

import lombok.Data;

@Data
public class ChangeUsernameRequest {
    private String newUsername;
}