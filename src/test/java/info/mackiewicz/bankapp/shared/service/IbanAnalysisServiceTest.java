package info.mackiewicz.bankapp.shared.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import info.mackiewicz.bankapp.account.util.IbanGenerator;
import info.mackiewicz.bankapp.transaction.model.TransactionType;

/**
 * Unit tests for IbanAnalysisService class.
 */
class IbanAnalysisServiceTest {

    private IbanAnalysisService ibanAnalysisService;

    // Example IBANs for testing
    private Iban sameOwnerIban1;
    private Iban sameOwnerIban2;
    private Iban differentOwnerSameBankIban;
    private Iban differentBankIban;

    @BeforeEach
    void setUp() {
        ibanAnalysisService = new IbanAnalysisService();

        // Preparing test data using IbanGenerator
        // Same owner, same bank - they have the same userId (123)
        sameOwnerIban1 = IbanGenerator.generateIban(123, 1);
        sameOwnerIban2 = IbanGenerator.generateIban(123, 2);

        // Different owner, same bank - has different userId (456)
        differentOwnerSameBankIban = IbanGenerator.generateIban(456, 1);

        // Different bank - using Iban.Builder() directly
        differentBankIban = new Iban.Builder()
                .countryCode(CountryCode.PL)
                .bankCode("101") // Different bank code than "485"
                .branchCode("1123")
                .nationalCheckDigit("4")
                .accountNumber("0000123456780000") // Same account number as sameOwnerIban2
                .build();
    }

    @Test
    @DisplayName("resolveTransferType should return TRANSFER_OWN when same owner")
    void resolveTransferType_SameOwner_ShouldReturnTransferOwn() {
        // when
        TransactionType result = ibanAnalysisService.resolveTransferType(sameOwnerIban1, sameOwnerIban2);

        // then
        assertEquals(TransactionType.TRANSFER_OWN, result);
    }

    @Test
    @DisplayName("resolveTransferType should return TRANSFER_INTERNAL when different owner in same bank")
    void resolveTransferType_DifferentOwnerSameBank_ShouldReturnTransferInternal() {
        // when
        TransactionType result = ibanAnalysisService.resolveTransferType(sameOwnerIban1, differentOwnerSameBankIban);

        // then
        assertEquals(TransactionType.TRANSFER_INTERNAL, result);
    }

    @Test
    @DisplayName("resolveTransferType should return TRANSFER_EXTERNAL when different bank")
    void resolveTransferType_DifferentBank_ShouldReturnTransferExternal() {
        // when
        TransactionType result = ibanAnalysisService.resolveTransferType(sameOwnerIban1, differentBankIban);

        // then
        assertEquals(TransactionType.TRANSFER_EXTERNAL, result);
    }

    @Test
    @DisplayName("isSameOwner should return true when same owner")
    void isSameOwner_SameOwner_ShouldReturnTrue() {
        // when
        boolean result = ibanAnalysisService.isSameOwner(sameOwnerIban1, sameOwnerIban2);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("isSameOwner should return false when different owner")
    void isSameOwner_DifferentOwner_ShouldReturnFalse() {
        // when
        boolean result = ibanAnalysisService.isSameOwner(sameOwnerIban1, differentOwnerSameBankIban);

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("isSameBank should return true when same bank")
    void isSameBank_SameBank_ShouldReturnTrue() {
        // when
        boolean result = ibanAnalysisService.isSameBank(sameOwnerIban1, differentOwnerSameBankIban);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("isSameBank should return false when different bank")
    void isSameBank_DifferentBank_ShouldReturnFalse() {
        // when
        boolean result = ibanAnalysisService.isSameBank(sameOwnerIban1, differentBankIban);

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("Diagnostic test verifying account numbers")
    void diagnosticTest_CheckAccountNumbers() {
        // Print diagnostic information
        System.out.println("sameOwnerIban1: " + sameOwnerIban1.toString());
        System.out.println("sameOwnerIban2: " + sameOwnerIban2.toString());
        System.out.println("differentOwnerSameBankIban: " + differentOwnerSameBankIban.toString());
        System.out.println("differentBankIban: " + differentBankIban.toString());
        
        System.out.println("sameOwnerIban1 accountNumber: " + sameOwnerIban1.getAccountNumber());
        System.out.println("sameOwnerIban2 accountNumber: " + sameOwnerIban2.getAccountNumber());
        System.out.println("differentOwnerSameBankIban accountNumber: " + differentOwnerSameBankIban.getAccountNumber());
        
        // Account number part responsible for owner identification (positions 4-13)
        System.out.println("sameOwnerIban1 owner part: " + sameOwnerIban1.getAccountNumber().substring(4, 14));
        System.out.println("sameOwnerIban2 owner part: " + sameOwnerIban2.getAccountNumber().substring(4, 14));
        System.out.println("differentOwnerSameBankIban owner part: " + differentOwnerSameBankIban.getAccountNumber().substring(4, 14));
        
        // Compare account number parts identifying the owner
        boolean sameOwnerCompare = sameOwnerIban1.getAccountNumber().regionMatches(4, sameOwnerIban2.getAccountNumber(), 4, 10);
        boolean differentOwnerCompare = sameOwnerIban1.getAccountNumber().regionMatches(4, differentOwnerSameBankIban.getAccountNumber(), 4, 10);
        
        System.out.println("Do sameOwnerIban1 and sameOwnerIban2 have the same owner: " + sameOwnerCompare);
        System.out.println("Do sameOwnerIban1 and differentOwnerSameBankIban have the same owner: " + differentOwnerCompare);
        
        // Verifications
        assertTrue(sameOwnerCompare, "IBANs of the same owner should have identical fragments from position 4 with length of 10 characters");
        assertFalse(differentOwnerCompare, "IBANs of different owners should have different fragments from position 4 with length of 10 characters");
    }
}