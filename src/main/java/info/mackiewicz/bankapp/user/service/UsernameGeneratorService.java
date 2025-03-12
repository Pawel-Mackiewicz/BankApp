package info.mackiewicz.bankapp.user.service;

import java.text.Normalizer;

import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.user.model.User;

/**
 * Service responsible for generating unique usernames based on user's personal information.
 * The generated username follows the pattern: firstname.lastname[uniqueID]
 * where:
 * - firstname and lastname are converted to lowercase and stripped of diacritical marks
 * - uniqueID is a 6-digit number derived from the user's email hash
 *
 * Example: "jan.kowalski123456"
 *
 * @see User
 */
@Service
public class UsernameGeneratorService {

    /**
     * Generates and sets a username for the given user based on their personal information.
     * The username is created using firstname, lastname and email.
     *
     * @param user The user object containing firstname, lastname and email
     * @return The user object with generated username set
     * @throws IllegalArgumentException if firstname, lastname or email is null
     */
    public User generateUsername(User user) {
        user.setUsername(generateUsername(user.getFirstname(), user.getLastname(), user.getEmail().toString()));
        return user;
    }
    
    /**
     * Generates a username from the provided personal information.
     * The username consists of firstname.lastname followed by a unique identifier.
     *
     * @param firstname The user's first name
     * @param lastname The user's last name
     * @param email The user's email address used for generating unique identifier
     * @return Generated username string
     * @throws IllegalArgumentException if any parameter is null
     */
    public String generateUsername(String firstname, String lastname, String email) {
        String baseUsername = generateBaseUsername(firstname, lastname);
        return baseUsername + generateUniqueID(email);
    }

    /**
     * Generates the base part of the username by combining firstname and lastname.
     * Removes diacritical marks and converts to lowercase.
     *
     * @param firstname The user's first name
     * @param lastname The user's last name
     * @return Base username in format "firstname.lastname"
     */
    private String generateBaseUsername(String firstname, String lastname) {
        return removeDiacritics(firstname.toLowerCase()) + "." + removeDiacritics(lastname.toLowerCase());
    }

    /**
     * Removes diacritical marks from text while preserving base characters.
     * For example: "żółć" becomes "zolc"
     *
     * @param text The text to process
     * @return Text with diacritical marks removed
     */
    private String removeDiacritics(String text) {
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }

    /**
     * Generates a unique identifier based on email hash.
     * Returns up to 6 digits to keep usernames reasonably short.
     *
     * @param email The email to use for generating unique ID
     * @return A string of up to 6 digits
     */
    private String generateUniqueID(String email) {
        int hash = email.hashCode();
        String sHash = Integer.toString(hash);
        
        return sHash.length() > 6 ? sHash.substring(0, 6) : sHash;
    }
}