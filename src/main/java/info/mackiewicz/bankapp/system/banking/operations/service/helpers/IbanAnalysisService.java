package info.mackiewicz.bankapp.system.banking.operations.service.helpers;

import info.mackiewicz.bankapp.shared.exception.IbanAnalysisException;
import info.mackiewicz.bankapp.shared.util.IbanMasker;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.iban4j.Iban;
import org.springframework.stereotype.Service;

/**
 * Service for analyzing and working with IBAN (International Bank Account
 * Number) data.
 * Provides functionality to determine relationships between accounts based on
 * their IBAN details.
 */
@Service
@Slf4j
public class IbanAnalysisService {

    /**
     * Resolves the type of transfer between two accounts based on their IBAN
     * details.
     *
     * @param sourceIban      The IBAN of the source account
     * @param destinationIban The IBAN of the destination account
     * @return TransactionType indicating whether this is an external transfer
     *         (different banks),
     *         internal transfer (same bank, but different owner), or own transfer
     *         (same owner, same bank)
     */
    public TransactionType resolveTransferType(Iban sourceIban, Iban destinationIban) {
        log.debug("Resolving transfer type for \n" +
                  "source IBAN: {}\n" +
                  "destination IBAN: {}", IbanMasker.maskIban(sourceIban), IbanMasker.maskIban(destinationIban));

        try {
            TransactionType type = !isSameBank(sourceIban, destinationIban) ? TransactionType.TRANSFER_EXTERNAL
                                 : !isSameOwner(sourceIban, destinationIban) ? TransactionType.TRANSFER_INTERNAL
                                 : TransactionType.TRANSFER_OWN;

            log.debug("Transfer type for \n" +
                     "source IBAN: {}\n" +
                     "destination IBAN: {}\n" +
                     "is {}", IbanMasker.maskIban(sourceIban), IbanMasker.maskIban(destinationIban), type);

            return type;
        } catch (Exception e) {
            throw new IbanAnalysisException(
                    String.format("Error analyzing IBANs: \n%s\n%s\n", sourceIban, destinationIban), e);
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
        // first 4 characters are always 0's and should be ignored in the comparison
        log.trace("Comparing account numbers for ownership: {} and {}", IbanMasker.maskIban(sourceIban),
                IbanMasker.maskIban(destinationIban));
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
        log.trace("Comparing bank codes and country codes: {} and {}", IbanMasker.maskIban(sourceIban), IbanMasker.maskIban(destinationIban));
        return sourceIban.getBankCode().equals(destinationIban.getBankCode())
                && sourceIban.getCountryCode().equals(destinationIban.getCountryCode());
    }
}