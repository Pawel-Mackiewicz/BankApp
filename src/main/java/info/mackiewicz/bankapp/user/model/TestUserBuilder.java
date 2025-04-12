package info.mackiewicz.bankapp.user.model;

import info.mackiewicz.bankapp.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.user.model.vo.Pesel;
import info.mackiewicz.bankapp.user.model.vo.PhoneNumber;

import java.time.LocalDate;
import java.util.Random;

/**
 * Builder dla klasy User przeznaczony do testów.
 * Zapewnia fluent API z wartościami domyślnymi dla szybkiego tworzenia obiektów testowych.
 */
public class TestUserBuilder {
    private static final Random RANDOM = new Random();
    private static final String DEFAULT_PASSWORD = "Test123!";
    private static final String DEFAULT_DOMAIN = "test.example.com";
    
    private String firstname = "Test";
    private String lastname = "User";
    private String password = DEFAULT_PASSWORD;
    private Pesel pesel = new Pesel(generateRandomPesel());
    private LocalDate dateOfBirth = LocalDate.of(1990, 1, 1);
    private EmailAddress email = new EmailAddress(generateRandomEmail());
    private PhoneNumber phoneNumber = new PhoneNumber(generateRandomPhoneNumber());
    
    /**
     * Tworzy nową instancję buildera.
     * 
     * @return nowa instancja TestUserBuilder
     */
    public static TestUserBuilder aUser() {
        return new TestUserBuilder();
    }
    
    /**
     * Ustawia imię użytkownika.
     * 
     * @param firstname imię
     * @return builder
     */
    public TestUserBuilder withFirstname(String firstname) {
        this.firstname = firstname;
        return this;
    }
    
    /**
     * Ustawia nazwisko użytkownika.
     * 
     * @param lastname nazwisko
     * @return builder
     */
    public TestUserBuilder withLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }
    
    /**
     * Ustawia hasło użytkownika.
     * 
     * @param password hasło
     * @return builder
     */
    public TestUserBuilder withPassword(String password) {
        this.password = password;
        return this;
    }
    
    /**
     * Ustawia PESEL użytkownika jako string.
     * 
     * @param pesel PESEL jako ciąg znaków
     * @return builder
     */
    public TestUserBuilder withPesel(String pesel) {
        this.pesel = new Pesel(pesel);
        return this;
    }
    
    /**
     * Ustawia PESEL użytkownika jako obiekt Pesel.
     * 
     * @param pesel PESEL jako obiekt value
     * @return builder
     */
    public TestUserBuilder withPesel(Pesel pesel) {
        this.pesel = pesel;
        return this;
    }
    
    /**
     * Ustawia datę urodzenia użytkownika.
     * 
     * @param dateOfBirth data urodzenia
     * @return builder
     */
    public TestUserBuilder withDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }
    
    /**
     * Ustawia adres email jako string.
     * 
     * @param email adres email
     * @return builder
     */
    public TestUserBuilder withEmail(String email) {
        this.email = new EmailAddress(email);
        return this;
    }
    
    /**
     * Ustawia adres email jako obiekt EmailAddress.
     * 
     * @param email adres email jako obiekt value
     * @return builder
     */
    public TestUserBuilder withEmail(EmailAddress email) {
        this.email = email;
        return this;
    }
    
    /**
     * Ustawia numer telefonu jako string.
     * 
     * @param phoneNumber numer telefonu
     * @return builder
     */
    public TestUserBuilder withPhoneNumber(String phoneNumber) {
        this.phoneNumber = new PhoneNumber(phoneNumber);
        return this;
    }
    
    /**
     * Ustawia numer telefonu jako obiekt PhoneNumber.
     * 
     * @param phoneNumber numer telefonu jako obiekt value
     * @return builder
     */
    public TestUserBuilder withPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }
    
    /**
     * Buduje i zwraca nowy obiekt User z ustawionymi wartościami.
     * 
     * @return nowo utworzony obiekt User
     */
    public User build() {
        User user = new User(password, pesel, firstname, lastname, dateOfBirth, email, phoneNumber);
        
        // Generowanie nazwy użytkownika na podstawie imienia i nazwiska z losowym przyrostkiem
        String username = generateUsername(firstname, lastname);
        user.setUsername(username);
        
        return user;
    }
    
    /**
     * Generuje losowy adres email.
     * 
     * @return wygenerowany adres email
     */
    private static String generateRandomEmail() {
        return generateRandomString(8) + "@" + DEFAULT_DOMAIN;
    }
    
    /**
     * Generuje losowy numer PESEL (11 cyfr).
     * 
     * @return wygenerowany numer PESEL
     */
    private static String generateRandomPesel() {
        StringBuilder pesel = new StringBuilder();
        for (int i = 0; i < 11; i++) {
            pesel.append(RANDOM.nextInt(10));
        }
        return pesel.toString();
    }
    
    /**
     * Generuje losowy numer telefonu (9 cyfr).
     * 
     * @return wygenerowany numer telefonu
     */
    private static String generateRandomPhoneNumber() {
        return String.format("%09d", RANDOM.nextInt(1000000000));
    }
    
    /**
     * Generuje nazwę użytkownika na podstawie imienia i nazwiska z losowym przyrostkiem.
     * 
     * @param firstname imię
     * @param lastname nazwisko
     * @return wygenerowana nazwa użytkownika
     */
    private static String generateUsername(String firstname, String lastname) {
        String base = firstname.toLowerCase() + "." + lastname.toLowerCase();
        String suffix = generateRandomString(4);
        return base + suffix;
    }
    
    /**
     * Generuje losowy ciąg znaków o podanej długości.
     * 
     * @param length długość ciągu
     * @return wygenerowany ciąg znaków
     */
    private static String generateRandomString(int length) {
        return RANDOM.ints(48, 122)
                .filter(i -> (i < 57 || i > 65) && (i < 90 || i > 97))
                .mapToObj(i -> (char) i)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
