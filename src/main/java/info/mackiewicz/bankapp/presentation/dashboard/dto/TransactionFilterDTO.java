package info.mackiewicz.bankapp.presentation.dashboard.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionFilterDTO {
    @NotNull
    private Integer accountId;
    
    @Min(0)
    @Builder.Default
    private int page = 0;
    
    @Min(1)
    @Builder.Default
    private int size = 20;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dateFrom;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dateTo;
    
    private String type;
    private BigDecimal amountFrom;
    private BigDecimal amountTo;
    private String query;
    
    @Builder.Default
    private String sortBy = "date";
    
    @Builder.Default
    private String sortDirection = "desc";
}