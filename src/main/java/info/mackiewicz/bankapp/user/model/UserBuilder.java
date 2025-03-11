package info.mackiewicz.bankapp.user.model;

import java.time.LocalDate;

import info.mackiewicz.bankapp.user.model.vo.Email;
import info.mackiewicz.bankapp.user.model.vo.Pesel;
import info.mackiewicz.bankapp.user.model.vo.PhoneNumber;

public class UserBuilder {

    private String firstname;
    private String lastname;
    private Pesel pesel;
    private Email email;
    private PhoneNumber phoneNumber;
    private String username;
    private String password;
    private LocalDate dateOfBirth;

    public UserBuilder withFirstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public UserBuilder withLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public UserBuilder withPesel(String pesel) {
        this.pesel = new Pesel(pesel);
        return this;
    }

    public UserBuilder withPesel(Pesel pesel) {
        this.pesel = pesel;
        return this;
    }

    public UserBuilder withDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public UserBuilder withEmail(String email) {
        this.email = new Email(email);
        return this;
    }

    public UserBuilder withEmail(Email email) {
        this.email = email;
        return this;
    }

    public UserBuilder withPhoneNumber(String phoneNumber) {
        this.phoneNumber = new PhoneNumber(phoneNumber);
        return this;
    }

    public UserBuilder withPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public UserBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public UserBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public User build() {
        return new User(username, password, pesel, firstname, lastname, dateOfBirth, email, phoneNumber);
    }
}
