package info.mackiewicz.bankapp.system.banking.history.dto;

import info.mackiewicz.bankapp.transaction.model.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.query.SortDirection;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for filtering transactions in history.
 * Allows specifying filtering criteria, sorting, and pagination parameters
 * when retrieving or exporting transactions.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    description = "Object containing parameters for filtering and paginating transactions",
    name = "TransactionFilter"
)
public class TransactionFilterDTO {

        @Schema(
            description = "Account ID for which to filter transactions",
            example = "49",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        private Integer accountId;

        @Schema(
            description = "Page number for pagination, starting from 0",
            defaultValue = "0",
            example = "0"
        )
        @Min(0)
        @Builder.Default
        private int page = 0;

        @Schema(
            description = "Number of items per page",
            defaultValue = "20",
            example = "20"
        )
        @Min(1)
        @Builder.Default
        private int size = 20;

        @Schema(
            description = "Start date for filtering transactions (inclusive)",
            example = "2023-01-01T00:00:00"
        )
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime dateFrom;

        @Schema(
            description = "End date for filtering transactions (inclusive)",
            example = "2023-12-31T23:59:59"
        )
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime dateTo;

        @Schema(
            description = "Type of transaction (e.g., TRANSFER_OWN, DEPOSIT, WITHDRAWAL)",
            example = "TRANSFER_OWN"
        )
        private TransactionType type;

        @Schema(
            description = "Minimum amount for filtering transactions",
            example = "100.00"
        )
        private BigDecimal amountFrom;

        @Schema(
            description = "Maximum amount for filtering transactions",
            example = "1000.00"
        )
        private BigDecimal amountTo;

        @Schema(
            description = "Search query for filtering transactions (searches in title and account details)",
            example = "Grocery Store"
        )
        private String query;

        @Schema(
            description = "Field to sort the transactions by (date, amount, type)",
            example = "date",
            defaultValue = "date"
        )
        @Builder.Default
        private String sortBy = "date";

        @Schema(
            description = "Sort direction (ASCENDING or DESCENDING)",
            example = "DESCENDING",
            defaultValue = "DESCENDING"
        )
        @Builder.Default
        private SortDirection sortDirection = SortDirection.DESCENDING;
}