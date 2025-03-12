package info.mackiewicz.bankapp.user.service;

import java.text.Normalizer;

import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.user.model.User;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
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
        log.debug("Starting username generation for user with email: {}", user.getEmail());
        String username = generateUsername(user.getFirstname(), user.getLastname(), user.getEmail().toString());
        user.setUsername(username);
        log.info("Generated username '{}' for user with email: {}", username, user.getEmail());
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
        log.debug("Generating username for firstname: {}, lastname: {}", firstname, lastname);
        String baseUsername = generateBaseUsername(firstname, lastname);
        log.debug("Generated base username: {}", baseUsername);
        String uniqueId = generateUniqueID(email);
        log.debug("Generated unique ID: {}", uniqueId);
        String fullUsername = baseUsername + uniqueId;
        log.debug("Final username: {}", fullUsername);
        return fullUsername;
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
     *
     * @param text The text to process
     * @return Text with diacritical marks removed
     */
    private String removeDiacritics(String text) {
        log.trace("Removing diacritics from: {}", text);
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        String result = normalized.replaceAll("\\p{M}", "");
        log.trace("Text after removing diacritics: {} -> {}", text, result);
        return result;
    }

    /**
     * Generates a unique identifier based on email hash.
     * Returns up to 6 digits to keep usernames reasonably short.
     *
     * @param email The email to use for generating unique ID
     * @return A string of up to 6 digits
     */
    private String generateUniqueID(String email) {
        log.trace("Generating unique ID for email: {}", email);
        int hash = email.hashCode();
        String sHash = Integer.toString(hash);
        String result = sHash.length() > 6 ? sHash.substring(0, 6) : sHash;
        log.trace("Generated unique ID: {} (from hash: {})", result, hash);
        return result;
    }
}