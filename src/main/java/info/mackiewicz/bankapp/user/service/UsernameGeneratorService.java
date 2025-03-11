package info.mackiewicz.bankapp.user.service;

import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.user.model.User;

@Service
public class UsernameGeneratorService {

    public User generateUsername(User user) {
        user.setUsername(generateUsername(user.getFirstname(), user.getLastname(), user.getEmail().toString()));
        return user;
    }
    
    public String generateUsername(String firstname, String lastname, String email) {
        String baseUsername = generateBaseUsername(firstname, lastname);
        return baseUsername + generateUniqueID(email);
    }

    private String generateBaseUsername(String firstname, String lastname) {
        return removeDiacritics(firstname.toLowerCase()) + "." + removeDiacritics(lastname.toLowerCase());
    }

    private String removeDiacritics(String text) {
        return text.replace("ą", "a")
                .replace("ć", "c")
                .replace("ę", "e")
                .replace("ł", "l")
                .replace("ń", "n")
                .replace("ó", "o")
                .replace("ś", "s")
                .replace("ź", "z")
                .replace("ż", "z");
    }

    private String generateUniqueID(String email) {
        int hash = email.hashCode();
        String sHash = Integer.toString(hash);
        
        return sHash.length() > 6 ? sHash.substring(0, 6) : sHash;
    }
}