package info.mackiewicz.bankapp.presentation.api.dashboard.controller;

import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.presentation.api.dashboard.controller.dto.WorkingBalanceResponse;
import info.mackiewicz.bankapp.presentation.api.dashboard.service.ApiDashboardService;
import info.mackiewicz.bankapp.system.shared.IdAccountAuthorizationService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Validated
public class ApiDashboardController implements ApiDashboardControllerInterface {

    private final ApiDashboardService dashboardService;
    private final IdAccountAuthorizationService authorizationService;

    @GetMapping("/account/{accountId}/balance/working")
    @Override
    public ResponseEntity<WorkingBalanceResponse> getWorkingBalance(
            @Min(1) @NotNull @PathVariable Integer accountId,
            @NotNull @AuthenticationPrincipal User owner
    ) {
        MDC.put("AccountID", accountId.toString());
        MDC.put("UserID", owner.getId().toString());
        try {
            // validating if owner has access to requested account information
            authorizationService.validateAccountOwnership(accountId, owner);

            BigDecimal workingBalance = dashboardService.getWorkingBalance(accountId);
            WorkingBalanceResponse response = new WorkingBalanceResponse(workingBalance, accountId);
            return ResponseEntity.ok().body(response);
        } finally {
            MDC.clear();
        }
    }
}
