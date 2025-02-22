package info.mackiewicz.bankapp.dto;

import lombok.Data;

@Data
public class InternalAccountTransferRequest {
    private Integer sourceAccountId;
    private String recipientIban;
    private String recipientEmail;
    private String amount;
    private String title;
}