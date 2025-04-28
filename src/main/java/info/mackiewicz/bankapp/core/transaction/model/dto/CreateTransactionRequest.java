package info.mackiewicz.bankapp.core.transaction.model.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class CreateTransactionRequest {
    private Integer sourceAccountId;
    private Integer destinationAccountId;
    private BigDecimal amount;
    private String type;
    private String title;
}
