package info.mackiewicz.bankapp.core.account.controllers;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class ApiDashboardController {

    private final ApiDashboardService dashboardService;

    @PreAuthorize("@idAccountAuthorizationService.validateAccountOwnership(#accountId, authentication.principal)")
    @GetMapping("/account/{accountId}/balance/working")
    public ResponseEntity<WorkingBalanceResponse> getWorkingBalance(@NonNull @PathVariable Integer accountId) {

        BigDecimal workingBalance = dashboardService.getWorkingBalance(accountId);
        WorkingBalanceResponse response = new WorkingBalanceResponse(workingBalance, accountId);
        return ResponseEntity.ok().body(response);
    }
}
