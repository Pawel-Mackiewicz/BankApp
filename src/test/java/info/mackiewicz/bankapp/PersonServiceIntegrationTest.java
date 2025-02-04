package info.mackiewicz.bankapp;

import info.mackiewicz.bankapp.model.Person;
import info.mackiewicz.bankapp.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PersonServiceIntegrationTest {

    @Autowired
    private PersonService personService;

    @Test
    public void testCreatePerson() {
        // Arrange: create a new Person object with required properties
        Person person = new Person();
        person.setName("Jan");
        person.setLastname("Kowalski");
        person.setDateOfBirth(LocalDate.of(1990, 1, 1));
        person.setPESEL("12345678901");

        // Act: save the person using PersonService (ID is auto-generated)
        Person savedPerson = personService.createPerson(person);

        // Assert: verify that the person has been saved correctly
        assertNotNull(savedPerson, "The saved person should not be null");
        assertNotNull(savedPerson.getId(), "The saved person must have an ID assigned");
        assertEquals("Jan", savedPerson.getName(), "The name should be 'Jan'");
        assertEquals("Kowalski", savedPerson.getLastname(), "The lastname should be 'Kowalski'");
        assertEquals("12345678901", savedPerson.getPESEL(), "The PESEL should match");
    }
}
