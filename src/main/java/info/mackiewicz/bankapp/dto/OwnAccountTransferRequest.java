package info.mackiewicz.bankapp.dto;

import lombok.Data;

@Data
public class OwnAccountTransferRequest {
    private Integer sourceAccountId;
    private Integer destinationAccountId;
    private String amount;
    private String title;
}