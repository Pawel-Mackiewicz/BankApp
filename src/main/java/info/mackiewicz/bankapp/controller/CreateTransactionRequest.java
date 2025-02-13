package info.mackiewicz.bankapp.controller;

import lombok.Data;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
@Component
@Data
public class CreateTransactionRequest {
    private Integer sourceAccountId;
    private Integer destinationAccountId;
    private BigDecimal amount;
    private String type;
    private String title;
}
