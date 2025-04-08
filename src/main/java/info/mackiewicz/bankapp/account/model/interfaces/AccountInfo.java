package info.mackiewicz.bankapp.account.model.interfaces;

/**
 * Interface representing basic account information.
 * This interface is used to retrieve essential details about a bank account.
 * It includes methods to get the IBAN and the owner's full name.
 */
public interface AccountInfo {

    /**
     * Retrieves the formatted IBAN of the account.
     * 
     * @return the formatted IBAN as a String.
     */
    String getFormattedIban();
    
    /**
 * Returns the full name of the account owner.
 *
 * @return the full name of the account owner as a String.
 */
    String getOwnerFullname();
}
