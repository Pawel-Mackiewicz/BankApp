package info.mackiewicz.bankapp.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountRequest {
    private int userId;
    private int newUserId;
}