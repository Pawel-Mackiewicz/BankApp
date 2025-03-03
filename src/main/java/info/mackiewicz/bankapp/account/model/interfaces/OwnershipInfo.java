package info.mackiewicz.bankapp.account.model.interfaces;

/**
 * Interface that defines the ownership information for bank accounts or other financial resources.
 * <p>
 * This interface provides methods for retrieving information about the owner of an account
 * or other financial resource, such as the owner's ID and name.
 * </p>
 * <p>
 * Implementations of this interface should provide the necessary information
 * to identify the owner of a resource in the banking system.
 * </p>
 */
public interface OwnershipInfo {

    /**
     * Returns the unique identifier of the owner.
     *
     * @return the unique ID of the owner as an Integer
     */
    Integer getOwnerId();

    /**
     * Returns the name of the owner.
     *
     * @return the name of the owner as a String
     */
    String getOwnerName();
}
