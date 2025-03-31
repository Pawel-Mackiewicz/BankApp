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
 * Testy jednostkowe dla klasy IbanAnalysisService.
 */
class IbanAnalysisServiceTest {

    private IbanAnalysisService ibanAnalysisService;

    // Przykładowe IBAN-y do testów
    private Iban sameOwnerIban1;
    private Iban sameOwnerIban2;
    private Iban differentOwnerSameBankIban;
    private Iban differentBankIban;

    @BeforeEach
    void setUp() {
        ibanAnalysisService = new IbanAnalysisService();

        // Przygotowanie danych testowych przy użyciu IbanGenerator
        // Ten sam właściciel, ten sam bank - mają ten sam userId (123)
        sameOwnerIban1 = IbanGenerator.generateIban(123, 1);
        sameOwnerIban2 = IbanGenerator.generateIban(123, 2);

        // Inny właściciel, ten sam bank - ma inny userId (456)
        differentOwnerSameBankIban = IbanGenerator.generateIban(456, 1);

        // Inny bank - używamy bezpośrednio Iban.Builder()
        differentBankIban = new Iban.Builder()
                .countryCode(CountryCode.PL)
                .bankCode("101") // Inny kod banku niż "485"
                .branchCode("1123")
                .nationalCheckDigit("4")
                .accountNumber("0000123456780000") // Taki sam numer konta jak sameOwnerIban2
                .build();
    }

    @Test
    @DisplayName("resolveTransferType powinien zwrócić TRANSFER_OWN gdy to ten sam właściciel")
    void resolveTransferType_SameOwner_ShouldReturnTransferOwn() {
        // when
        TransactionType result = ibanAnalysisService.resolveTransferType(sameOwnerIban1, sameOwnerIban2);

        // then
        assertEquals(TransactionType.TRANSFER_OWN, result);
    }

    @Test
    @DisplayName("resolveTransferType powinien zwrócić TRANSFER_INTERNAL gdy inny właściciel w tym samym banku")
    void resolveTransferType_DifferentOwnerSameBank_ShouldReturnTransferInternal() {
        // when
        TransactionType result = ibanAnalysisService.resolveTransferType(sameOwnerIban1, differentOwnerSameBankIban);

        // then
        assertEquals(TransactionType.TRANSFER_INTERNAL, result);
    }

    @Test
    @DisplayName("resolveTransferType powinien zwrócić TRANSFER_EXTERNAL gdy inny bank")
    void resolveTransferType_DifferentBank_ShouldReturnTransferExternal() {
        // when
        TransactionType result = ibanAnalysisService.resolveTransferType(sameOwnerIban1, differentBankIban);

        // then
        assertEquals(TransactionType.TRANSFER_EXTERNAL, result);
    }

    @Test
    @DisplayName("isSameOwner powinien zwrócić true gdy to ten sam właściciel")
    void isSameOwner_SameOwner_ShouldReturnTrue() {
        // when
        boolean result = ibanAnalysisService.isSameOwner(sameOwnerIban1, sameOwnerIban2);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("isSameOwner powinien zwrócić false gdy to inny właściciel")
    void isSameOwner_DifferentOwner_ShouldReturnFalse() {
        // when
        boolean result = ibanAnalysisService.isSameOwner(sameOwnerIban1, differentOwnerSameBankIban);

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("isSameBank powinien zwrócić true gdy to ten sam bank")
    void isSameBank_SameBank_ShouldReturnTrue() {
        // when
        boolean result = ibanAnalysisService.isSameBank(sameOwnerIban1, differentOwnerSameBankIban);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("isSameBank powinien zwrócić false gdy to inny bank")
    void isSameBank_DifferentBank_ShouldReturnFalse() {
        // when
        boolean result = ibanAnalysisService.isSameBank(sameOwnerIban1, differentBankIban);

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("Test diagnostyczny weryfikujący numery kont")
    void diagnosticTest_CheckAccountNumbers() {
        // Wypisanie informacji diagnostycznych
        System.out.println("sameOwnerIban1: " + sameOwnerIban1.toString());
        System.out.println("sameOwnerIban2: " + sameOwnerIban2.toString());
        System.out.println("differentOwnerSameBankIban: " + differentOwnerSameBankIban.toString());
        System.out.println("differentBankIban: " + differentBankIban.toString());
        
        System.out.println("sameOwnerIban1 accountNumber: " + sameOwnerIban1.getAccountNumber());
        System.out.println("sameOwnerIban2 accountNumber: " + sameOwnerIban2.getAccountNumber());
        System.out.println("differentOwnerSameBankIban accountNumber: " + differentOwnerSameBankIban.getAccountNumber());
        
        // Fragment numeru konta odpowiadający za identyfikację właściciela (pozycje 4-13)
        System.out.println("sameOwnerIban1 owner part: " + sameOwnerIban1.getAccountNumber().substring(4, 14));
        System.out.println("sameOwnerIban2 owner part: " + sameOwnerIban2.getAccountNumber().substring(4, 14));
        System.out.println("differentOwnerSameBankIban owner part: " + differentOwnerSameBankIban.getAccountNumber().substring(4, 14));
        
        // Porównanie części numeru konta identyfikującej właściciela
        boolean sameOwnerCompare = sameOwnerIban1.getAccountNumber().regionMatches(4, sameOwnerIban2.getAccountNumber(), 4, 10);
        boolean differentOwnerCompare = sameOwnerIban1.getAccountNumber().regionMatches(4, differentOwnerSameBankIban.getAccountNumber(), 4, 10);
        
        System.out.println("Czy sameOwnerIban1 i sameOwnerIban2 mają tego samego właściciela: " + sameOwnerCompare);
        System.out.println("Czy sameOwnerIban1 i differentOwnerSameBankIban mają tego samego właściciela: " + differentOwnerCompare);
        
        // Weryfikacje
        assertTrue(sameOwnerCompare, "IBAN-y tego samego właściciela powinny mieć identyczne fragmenty od pozycji 4 o długości 10 znaków");
        assertFalse(differentOwnerCompare, "IBAN-y różnych właścicieli powinny mieć różne fragmenty od pozycji 4 o długości 10 znaków");
    }
}