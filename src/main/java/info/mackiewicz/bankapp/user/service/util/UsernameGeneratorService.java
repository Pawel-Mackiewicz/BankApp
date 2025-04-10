package info.mackiewicz.bankapp.user.service.util;

import com.ibm.icu.text.Transliterator;
import info.mackiewicz.bankapp.user.exception.UsernameException;
import info.mackiewicz.bankapp.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service responsible for generating unique usernames based on user's personal information.
 * The generated username follows the pattern: firstname.lastname[uniqueID]
 * where:
 * - firstname and lastname are converted to lowercase and stripped of diacritical marks
 * - uniqueID is a 6-digit number derived from the user's email hash
 * <p>
 * Example: "jan.kowalski123456"
 *
 * @see User
 */
@Slf4j
@Service
public class UsernameGeneratorService {


    @Deprecated
    public User generateUsername(User user) {
        String username = generateUsername(user.getFirstname(), user.getLastname(), user.getEmail().toString());
        user.setUsername(username);
        return user;
    }

    /**
     * Generates a username from the provided personal information.
     * The username consists of firstname.lastname followed by a unique identifier.
     *
     * @param firstname The user's first name
     * @param lastname  The user's last name
     * @param email     The user's email address used for generating unique identifier
     * @return Generated username string
     * @throws UsernameException if any argument is null or empty
     */
    public String generateUsername(String firstname, String lastname, String email) {
        if (isEmpty(firstname) || isEmpty(lastname) || isEmpty(email)) {
            throw new info.mackiewicz.bankapp.user.exception.UsernameException("Firstname, lastname and email cannot be null or empty");
        }
        log.debug("Generating username...");
        String baseUsername = generateBaseUsername(firstname, lastname);
        log.trace("Generated base username: {}", baseUsername);
        String uniqueId = generateUniqueID(email);
        log.trace("Generated unique ID: {}", uniqueId);
        String fullUsername = baseUsername + uniqueId;
        log.trace("Final username: {}", fullUsername);
        return fullUsername;
    }

    private boolean isEmpty(String string) {
        return string == null || string.isBlank();
    }

    /**
     * Generates the base part of the username by combining firstname and lastname.
     * Removes diacritical marks and converts to lowercase.
     *
     * @param firstname The user's first name
     * @param lastname  The user's last name
     * @return Base username in format "firstname.lastname"
     */
    private String generateBaseUsername(String firstname, String lastname) {
        log.trace("Processing firstname: {}, lastname: {}", firstname, lastname);
        String processedFirstname = removeDiacritics(firstname.toLowerCase());
        String processedLastname = removeDiacritics(lastname.toLowerCase());
        String baseUsername = processedFirstname + "." + processedLastname;
        log.trace("Generated base username: {} (from {} {})", baseUsername, firstname, lastname);
        return baseUsername;
    }

    /**
     * Removes diacritical marks from text while preserving base characters.
     * For example: "żółć" becomes "zolc"
     * Also handles special characters and spaces:
     * - Removes apostrophes and other special characters
     * - Replaces spaces with hyphens
     *
     * @param text The text to process
     * @return Text with diacritical marks and special characters removed
     */
    private String removeDiacritics(String text) {
        log.trace("Removing diacritics from: {}", text);
        if (text == null) {
            return "";
        }

        log.trace("Removing diacritics from: {}", text);
        Transliterator transliterator = Transliterator.getInstance("Any-Latin; Latin-ASCII");
        String transformedText = transliterator.transform(text);
        transformedText = transformedText.replaceAll("[^a-zA-Z0-9]", "");
        log.trace("Diacritics removed: {}", transformedText);
        return transformedText;
    }

    /**
     * Generates a unique identifier based on email hash.
     * Returns exactly 6 digits, using modulo to ensure consistent length.
     *
     * @param email The email to use for generating unique ID
     * @return A string of exactly 6 digits
     */
    private String generateUniqueID(String email) {
        log.trace("Generating unique ID for email: {}", email);
        int hash = Math.abs(email.hashCode());
        String sHash = String.format("%06d", hash % 1000000);
        log.trace("Generated unique ID: {} (from hash: {})", sHash, hash);
        return sHash;
    }
}