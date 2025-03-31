package info.mackiewicz.bankapp.presentation.dashboard.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.query.SortDirection;
import org.springframework.format.annotation.DateTimeFormat;

import info.mackiewicz.bankapp.transaction.model.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionFilterDTO {

        @Schema(description = "Account ID for which to filter transactions", example = "49", required = true)
        @NotNull
        private Integer accountId;

        @Schema(description = "Page number for pagination, starting from 0", defaultValue = "0", required = false)
        @Min(0)
        @Builder.Default
        private int page = 0;

        @Schema(description = "Number of items per page", defaultValue = "20", required = false)
        @Min(1)
        @Builder.Default
        private int size = 20;

        @Schema(description = "Start date for filtering transactions", example = "2023-01-01T00:00:00", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime dateFrom;

        @Schema(description = "End date for filtering transactions", example = "2023-12-31T23:59:59", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime dateTo;

        @Schema(description = "Type of transaction", required = false)
        private TransactionType type;

        @Schema(description = "Minimum amount for filtering transactions", example = "100.00", required = false)
        private BigDecimal amountFrom;

        @Schema(description = "Maximum amount for filtering transactions", example = "1000.00", required = false)
        private BigDecimal amountTo;

        @Schema(description = "Search query for filtering transactions", example = "Grocery", required = false)
        private String query;

        @Schema(description = "Field to sort the transactions by", example = "date", required = false)
        @Builder.Default
        private String sortBy = "date";

        @Schema(description = "Sort direction (asc or desc)", example = "asc", defaultValue = "DESCENDING", required = false)
        @Builder.Default
        private SortDirection sortDirection = SortDirection.DESCENDING;
}