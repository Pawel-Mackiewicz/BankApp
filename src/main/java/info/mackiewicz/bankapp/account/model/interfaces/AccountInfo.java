package info.mackiewicz.bankapp.account.model.interfaces;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Interface representing basic account information.
 * This interface is used to retrieve essential details about a bank account.
 * It includes methods to get the IBAN and the owner's full name.
 */
public interface AccountInfo {

    @Schema(description = "The unique ID of the account.", example = "23")
    Integer getId();

    /**
     * Retrieves the formatted IBAN of the account.
     * 
     * @return the formatted IBAN as a String.
     */
    @Schema(description = "The IBAN of the account formatted with spaces.", example = "PL64 4851 1234 0003 5700 0000 0003")
    String getFormattedIban();
    
    /**
     * Retrieves the account owner's first and last name.
     *
     * @return the owner's full name as a String.
     */
    @Schema(description = "The full name of the account owner.", example = "John Doe")
    String getOwnerFullname();
}
