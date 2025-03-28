package info.mackiewicz.bankapp.presentation.dashboard.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionFilterDTO {
    @NotNull
    private Integer accountId;
    
    @Schema(description = "Page number for pagination, starting from 0", 
            example = "0",
            defaultValue = "0")
    @Min(0)
    @Builder.Default
    private int page = 0;
    
    @Schema(description = "Number of items per page", 
            example = "20",
            defaultValue = "20")
    @Min(1)
    @Builder.Default
    private int size = 20;
    
    @Schema(description = "Start date for filtering transactions", 
            example = "2023-01-01T00:00:00")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dateFrom;
    
    @Schema(description = "End date for filtering transactions", 
            example = "2023-12-31T23:59:59")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dateTo;
    
    @Schema(description = "Type of transaction", 
            example = "DEPOSIT")
    private String type;

    @Schema(description = "Minimum amount for filtering transactions", 
            example = "100.00")
    private BigDecimal amountFrom;

    @Schema(description = "Maximum amount for filtering transactions", 
            example = "1000.00")
    private BigDecimal amountTo;

    @Schema(description = "Search query for filtering transactions", 
            example = "Grocery")
    private String query;
    
    @Schema(description = "Field to sort the transactions by", 
            example = "date")
    @Builder.Default
    private String sortBy = "date";
    
    @Schema(description = "Sort direction (asc or desc)", 
            example = "asc",
            defaultValue = "desc")
    @Builder.Default
    private String sortDirection = "desc";
}