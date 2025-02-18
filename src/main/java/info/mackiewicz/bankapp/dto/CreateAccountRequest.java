package info.mackiewicz.bankapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountRequest {
    private int userId;
    private int newUserId;
}