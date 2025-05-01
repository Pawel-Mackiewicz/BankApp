package info.mackiewicz.bankapp.presentation.api.dashboard.controller.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents the response containing the working balance information for a given account.
 * This includes details about the currently calculated working balance, the associated
 * account ID, and the timestamp when the response was generated.
 * <p>
 * Instances of this class are immutable after construction, except for the timestamp,
 * which is initialized to the current time at the moment of object creation.
 */
@Getter
@RequiredArgsConstructor
public class WorkingBalanceResponse {
    final BigDecimal workingBalance;
    final int accountId;
    LocalDateTime timestamp = LocalDateTime.now();
}
