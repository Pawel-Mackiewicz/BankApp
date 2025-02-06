package info.mackiewicz.bankapp.controller;

import lombok.Data;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
@Component
@Data
public class CreateTransactionRequest {
    private int fromAccountId;
    private int toAccountId;
    private BigDecimal amount;
    private String type;
}
