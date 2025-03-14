package info.mackiewicz.bankapp.user.model;

import java.time.LocalDate;

import org.hibernate.validator.constraints.pl.PESEL;

import info.mackiewicz.bankapp.user.exception.InvalidUserDataException;
import info.mackiewicz.bankapp.user.model.vo.Email;
import info.mackiewicz.bankapp.user.model.vo.Pesel;
import info.mackiewicz.bankapp.user.model.vo.PhoneNumber;

/**
 * Step builder pattern implementation for creating User objects.
 */
public class UserBuilder {

    /**
     * Entry point for the User builder.
     * Initializes the step builder process.
     * 
     * @return FirstnameStep interface to begin building a User object
     */
    public static FirstnameStep builder() {
        return new Steps();
    }
    
    // Interfejsy definiujące poszczególne kroki
    public interface FirstnameStep {
        /**
         * Sets the user's first name.
         * 
         * @param firstname user's first name
         * @return LastnameStep to continue building the User
         */
        LastnameStep withFirstname(String firstname);
    }
    
    public interface LastnameStep {
        /**
         * Sets the user's last name.
         * 
         * @param lastname user's last name
         * @return PeselStep to continue building the User
         */
        PeselStep withLastname(String lastname);
    }
    
    public interface PeselStep {
        /**
         * Sets the user's PESEL number as a string.
         * 
         * @param pesel user's PESEL number as a string
         * @return DateOfBirthStep to continue building the User
         */
        DateOfBirthStep withPesel(String pesel);
        
        /**
         * Sets the user's PESEL using a Pesel value object.
         * 
         * @param pesel user's PESEL as a value object
         * @return DateOfBirthStep to continue building the User
         */
        DateOfBirthStep withPesel(Pesel pesel);
    }
    
    public interface DateOfBirthStep {
        /**
         * Sets the user's date of birth.
         * 
         * @param dateOfBirth user's date of birth
         * @return EmailStep to continue building the User
         */
        EmailStep withDateOfBirth(LocalDate dateOfBirth);
    }
    
    public interface EmailStep {
        /**
         * Sets the user's email address as a string.
         * 
         * @param email user's email address as a string
         * @return PhoneNumberStep to continue building the User
         */
        PhoneNumberStep withEmail(String email);
        
        /**
         * Sets the user's email using an Email value object.
         * 
         * @param email user's email as a value object
         * @return PhoneNumberStep to continue building the User
         */
        PhoneNumberStep withEmail(Email email);
    }
    
    public interface PhoneNumberStep {
        /**
         * Sets the user's phone number as a string.
         * 
         * @param phoneNumber user's phone number as a string
         * @return PasswordStep to continue building the User
         */
        PasswordStep withPhoneNumber(String phoneNumber);
        
        /**
         * Sets the user's phone number using a PhoneNumber value object.
         * 
         * @param phoneNumber user's phone number as a value object
         * @return PasswordStep to continue building the User
         */
        PasswordStep withPhoneNumber(PhoneNumber phoneNumber);
    }
    
    public interface PasswordStep {
        /**
         * Sets the user's password.
         * 
         * @param password user's password
         * @return BuildStep to finalize building the User
         */
        BuildStep withPassword(String password);
    }
    
    public interface BuildStep {
        /**
         * Validates all input data and creates a new User object.
         * 
         * @return fully constructed User object
         * @throws InvalidUserDataException if any validation fails
         */
        User build();
    }
    
    // All steps are implemented in a single class
    private static class Steps implements 
            FirstnameStep, LastnameStep, PeselStep, DateOfBirthStep,
            EmailStep, PhoneNumberStep, PasswordStep, BuildStep {
        
        private String firstname;
        private String lastname;
        @PESEL
        private Pesel pesel;
        private Email email;
        private PhoneNumber phoneNumber;
        private String password;
        private LocalDate dateOfBirth;
        
        @Override
        public LastnameStep withFirstname(String firstname) {
            this.firstname = firstname;
            return this;
        }
        
        @Override
        public PeselStep withLastname(String lastname) {
            this.lastname = lastname;
            return this;
        }
        
        @Override
        public DateOfBirthStep withPesel(String pesel) {
            this.pesel = new Pesel(pesel);
            return this;
        }
        
        @Override
        public DateOfBirthStep withPesel(Pesel pesel) {
            this.pesel = pesel;
            return this;
        }
        
        @Override
        public EmailStep withDateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }
        
        @Override
        public PhoneNumberStep withEmail(String email) {
            this.email = new Email(email);
            return this;
        }
        
        @Override
        public PhoneNumberStep withEmail(Email email) {
            this.email = email;
            return this;
        }
        
        @Override
        public PasswordStep withPhoneNumber(String phoneNumber) {
            this.phoneNumber = new PhoneNumber(phoneNumber);
            return this;
        }
        
        @Override
        public PasswordStep withPhoneNumber(PhoneNumber phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }
        
        @Override
        public BuildStep withPassword(String password) {
            this.password = password;
            return this;
        }
        
        @Override
        public User build() {
            validate();
            return new User(password, pesel, firstname, lastname, dateOfBirth, email, phoneNumber);
        }

        private void validate() {
            if (firstname == null || firstname.trim().isEmpty()) {
                throw new InvalidUserDataException("First name cannot be empty");
            }
            if (lastname == null || lastname.trim().isEmpty()) {
                throw new InvalidUserDataException("Last name cannot be empty");
            }
            if (pesel == null) {
                throw new InvalidUserDataException("PESEL is required");
            }
            if (dateOfBirth == null) {
                throw new InvalidUserDataException("Date of birth is required");
            }
            if (email == null) {
                throw new InvalidUserDataException("Email address is required");
            }
            if (phoneNumber == null) {
                throw new InvalidUserDataException("Phone number is required");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new InvalidUserDataException("Password cannot be empty");
            }
        }
    }
}