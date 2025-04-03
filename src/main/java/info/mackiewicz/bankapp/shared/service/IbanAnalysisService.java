package info.mackiewicz.bankapp.shared.service;

import org.iban4j.Iban;
import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.shared.exception.IbanAnalysisException;
import info.mackiewicz.bankapp.transaction.model.TransactionType;

/**
 * Service for analyzing and working with IBAN (International Bank Account Number) data.
 * Provides functionality to determine relationships between accounts based on their IBAN details.
 */
@Service
public class IbanAnalysisService {
    
    /**
     * Resolves the type of transfer between two accounts based on their IBAN details.
     *
     * @param sourceIban      The IBAN of the source account
     * @param destinationIban The IBAN of the destination account
     * @return TransactionType indicating whether this is an external transfer (different banks),
     *         internal transfer (same bank but different owner), or own transfer (same owner)
     */
    public TransactionType resolveTransferType(Iban sourceIban, Iban destinationIban) {
        try {
        return !isSameBank(sourceIban, destinationIban) ? TransactionType.TRANSFER_EXTERNAL 
            : !isSameOwner(sourceIban, destinationIban) ? TransactionType.TRANSFER_INTERNAL
            : TransactionType.TRANSFER_OWN;
        } catch (Exception e) {
            throw new IbanAnalysisException(String.format("Error analyzing IBANs: \n%s\n%s\n", sourceIban, destinationIban), e);
        }
    }

    /**
     * Determines if two accounts belong to the same owner by comparing
     * it's account numbers.
     *
     * @param sourceIban      The IBAN of the first account
     * @param destinationIban The IBAN of the second account
     * @return true if both accounts belong to the same owner, false otherwise
     */
    public boolean isSameOwner(Iban sourceIban, Iban destinationIban) {
        return sourceIban.getAccountNumber().regionMatches(4, destinationIban.getAccountNumber(), 4, 10);
    }

    /**
     * Determines if two accounts belong to the same bank by comparing
     * their bank codes and country codes.
     *
     * @param sourceIban      The IBAN of the first account
     * @param destinationIban The IBAN of the second account
     * @return true if both accounts belong to the same bank, false otherwise
     */
    public boolean isSameBank(Iban sourceIban, Iban destinationIban) {
        return sourceIban.getBankCode().equals(destinationIban.getBankCode())
                && sourceIban.getCountryCode().equals(destinationIban.getCountryCode());
    }
}