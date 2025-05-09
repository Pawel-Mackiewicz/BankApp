package info.mackiewicz.bankapp.presentation.dashboard.controller;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.account.service.AccountService;
import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import info.mackiewicz.bankapp.core.transaction.model.TransactionType;
import info.mackiewicz.bankapp.core.transaction.service.TransactionService;
import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.presentation.dashboard.dto.InternalTransferRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.OwnTransferRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.WebTransferRequest;
import info.mackiewicz.bankapp.presentation.dashboard.service.transfer.assembler.TransactionAssembler;
import info.mackiewicz.bankapp.system.transaction.processing.TransactionProcessingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/dashboard/transfer")
public class TransferController {

    private final TransactionService transactionService;
    private final TransactionProcessingService transactionProcessingService;
    private final TransactionAssembler transactionAssembler;
    private final AccountService accountService;

    @PostMapping("/own")
    public String handleOwnTransfer(
            @AuthenticationPrincipal User user,
            OwnTransferRequest request,
            RedirectAttributes redirectAttributes) {
        log.info("Processing own transfer request for user: {}, source account: {}, destination account: {}",
                user.getId(), request.getSourceAccountId(), request.getDestinationAccountId());
        try {
            log.debug("Validating account ownership for source account: {}", request.getSourceAccountId());
            validateAccountOwnership(user, request.getSourceAccountId());
            
            log.debug("Validating account ownership for destination account: {}", request.getDestinationAccountId());
            validateAccountOwnership(user, request.getDestinationAccountId());

            log.debug("Assembling own transfer transaction");
            Transaction transaction = transactionAssembler.assembleOwnTransfer(request);

            log.debug("Creating transaction in service");
            transactionService.registerTransaction(transaction);

            log.debug("Processing transaction...");
            transactionProcessingService.processTransactionById(transaction.getId());

            log.info("Successfully created, registered and performed own transfer transaction with ID: {}", transaction.getId());
            redirectAttributes.addFlashAttribute("transferSuccessMessage",
                    "Transfer between own accounts created successfully");
        } catch (Exception e) {
            log.error("Failed to process own transfer: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/internal")
    public String handleInternalTransfer(
            @AuthenticationPrincipal User user,
            @Valid InternalTransferRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        log.info("Processing internal transfer request for user: {}, source IBAN: {}, destination: {}",
                user.getId(), request.getSourceIban(),
                request.getRecipientIban() != null ? request.getRecipientIban() : request.getRecipientEmail());
        
        try {
            log.debug("Validating transfer request format");
            if (!request.isValid()) {
                log.warn("Invalid transfer request - missing both IBAN and email");
                throw new IllegalArgumentException("Either IBAN or email must be provided");
            }

            log.debug("Checking for validation errors");
            if (bindingResult.hasErrors()) {
                log.warn("Validation errors found in transfer request:");
                bindingResult.getAllErrors().forEach(error ->
                    log.warn("Validation error: {}", error.getDefaultMessage())
                );
                throw new IllegalArgumentException("Invalid transfer request");
            }

            log.debug("Validating source account ownership");
            validateAccountOwnership(user, request.getSourceIban());

            log.debug("Assembling internal transfer transaction");
            Transaction transaction = transactionAssembler.assembleInternalTransfer(request);
            
            log.debug("Creating transaction in service with amount: {}", request.getAmount());
            transactionService.registerTransaction(transaction);


            if (transaction.getType() == TransactionType.TRANSFER_OWN) {
                transactionProcessingService.processTransactionById(transaction.getId());
                log.debug("Processing transaction...");
            }
            String type = transaction.getType().getDisplayName();
            log.info("Successfully created {} transaction with ID: {}", type, transaction.getId());
            redirectAttributes.addFlashAttribute("transferSuccessMessage", String.format("%s created successfully", type));
        } catch (Exception e) {
            log.error("Failed to process transfer: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/external")
    public String handleExternalTransfer(
            @AuthenticationPrincipal User user,
            @Valid WebTransferRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        log.info("Processing external transfer request for user: {}, source IBAN: {}, recipient IBAN: {}, amount: {}",
                user.getId(), request.getSourceIban(), request.getRecipientIban(), request.getAmount());
        
        try {
            log.debug("Validating source account ownership");
            validateAccountOwnership(user, request.getSourceIban());

            log.debug("Assembling external transfer transaction");
            Transaction transaction = transactionAssembler.assembleExternalTransfer(request);

            log.debug("Creating transaction in service");
            transactionService.registerTransaction(transaction);
            
            log.info("Successfully created external transfer transaction with ID: {}", transaction.getId());
            redirectAttributes.addFlashAttribute("transferSuccessMessage", "External transfer created successfully");
        } catch (Exception e) {
            log.error("Failed to process external transfer: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    private void validateAccountOwnership(User user, Integer accountId) {
        log.debug("Validating account ownership by ID for user: {} and account: {}", user.getId(), accountId);
        
        boolean hasAccess = accountService.getAccountById(accountId)
        .getOwner()
        .getId()
        .equals(user.getId());
                
        if (!hasAccess) {
            log.warn("Access denied - user: {} does not own account: {}", user.getId(), accountId);
            throw new IllegalArgumentException("You don't have permission to this account");
        }
        log.debug("Account ownership validation successful for account ID: {}", accountId);
    }

    private void validateAccountOwnership(User user, String accountIban) {
        log.debug("Validating account ownership by IBAN for user: {} and IBAN: {}", user.getId(), accountIban);
        
        try {
            // Convert String to Iban object for proper comparison
            final String ibanString = accountIban;
            boolean hasAccess = accountService.getAccountsByOwnersId(user.getId()).stream()
                    .map(Account::getIban)
                    .anyMatch(iban -> iban != null && iban.toString().equals(ibanString));
                    
            if (!hasAccess) {
                log.warn("Access denied - user: {} does not own account with IBAN: {}", user.getId(), accountIban);
                throw new IllegalArgumentException("You don't have permission to this account");
            }
            log.debug("Account ownership validation successful for IBAN: {}", accountIban);
        } catch (Exception e) {
            log.error("Error validating account ownership - user: {}, IBAN: {} - Error: {}",
                    user.getId(), accountIban, e.getMessage(), e);
            throw new IllegalArgumentException("Invalid IBAN format or you don't have permission to this account");
        }
    }
}