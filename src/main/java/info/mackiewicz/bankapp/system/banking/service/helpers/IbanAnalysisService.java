package info.mackiewicz.bankapp.system.banking.service.helpers;

import org.iban4j.Iban;
import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.shared.exception.IbanAnalysisException;
import info.mackiewicz.bankapp.shared.util.IbanMasker;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import lombok.extern.slf4j.Slf4j;

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
     * Determines the transfer type between two IBANs as external, internal, or own.
     * <p>
     * A transfer is classified as:
     * <ul>
     *   <li>{@code TRANSFER_EXTERNAL} if the IBANs belong to different banks,</li>
     *   <li>{@code TRANSFER_INTERNAL} if they belong to the same bank but different owners,</li>
     *   <li>{@code TRANSFER_OWN} if they belong to the same bank and owner.</li>
     * </ul>
     * <p>
     * If an error occurs during the analysis, an {@code IbanAnalysisException} is thrown.
     *
     * @param sourceIban      the IBAN of the source account
     * @param destinationIban the IBAN of the destination account
     * @return the determined {@code TransactionType} indicating the nature of the transfer
     * @throws IbanAnalysisException if an error occurs while resolving the transfer type
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
     * Checks if the two provided IBAN accounts are owned by the same individual.
     *
     * <p>Compares the account numbers while ignoring the first four characters,
     * which are always zeros.</p>
     *
     * @param sourceIban the IBAN of the first account
     * @param destinationIban the IBAN of the second account
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