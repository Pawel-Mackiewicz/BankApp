package info.mackiewicz.bankapp.transaction.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import info.mackiewicz.bankapp.transaction.model.dto.BankingOperationRequest;
import info.mackiewicz.bankapp.transaction.model.dto.EmailTransferRequest;
import info.mackiewicz.bankapp.transaction.model.dto.IbanTransferRequest;

/**
 * Interface defining basic banking operations API endpoints.
 * Provides contract for money transfers, withdrawals and deposits.
 */
public interface BankingOperationControllerInterface {

    /**
     * Transfers funds between accounts
     * 
     * @param request transfer details including source account IBAN, destination account IBAN and amount
     * @param authUser authenticated user details, who has access to the source account
     * @return response with transaction result
     */
    ResponseEntity<?> ibanTransfer(IbanTransferRequest request, UserDetails authUser);

    /**
     * Transfers funds to an email address
     * 
     * @param request transfer details including source account, destination email (which will be resolved to IBAN) and amount
     * @param authUser authenticated user details, who has access to the source account
     * @return response with transaction result
     */
    ResponseEntity<?> emailTransfer(EmailTransferRequest request, UserDetails authUser);
    
    /**
     * Withdraws funds from an account
     * 
     * @param request withdrawal details including account and amount
     * @param authUser authenticated user details, who has access to the account
     * @return response with transaction result
     */
    ResponseEntity<?> withdraw(BankingOperationRequest request, UserDetails authUser);
    
    /**
     * Deposits funds to an account
     * 
     * @param request deposit details including account and amount
     * @param authUser authenticated user details, who has access to the account
     * @return response with transaction result
     */
    ResponseEntity<?> deposit(BankingOperationRequest request, UserDetails authUser);
}