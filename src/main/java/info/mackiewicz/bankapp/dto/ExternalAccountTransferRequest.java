package info.mackiewicz.bankapp.dto;

import lombok.Data;

@Data
public class ExternalAccountTransferRequest {
    private Integer sourceAccountId;
    private String recipientIban;
    private String amount;
    private String title;
}