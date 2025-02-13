package info.mackiewicz.bankapp.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TransferForm {
    @NotNull(message = "Source account is required")
    private Integer sourceAccountId;
    
    @NotNull(message = "Recipient account number is required")
    private Integer recipientAccountId;
    
    @NotEmpty(message = "Recipient name is required")
    private String recipientName;
    
    @NotEmpty(message = "Title is required")
    private String title;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
}
