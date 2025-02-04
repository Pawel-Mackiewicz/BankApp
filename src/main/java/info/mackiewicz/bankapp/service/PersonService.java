package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.model.Person;
import info.mackiewicz.bankapp.repository.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
//BY chatGPT
@Service
public class PersonService {

    private final PersonRepository personRepository;

    // Constructor injection of PersonRepository
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Creates a new Person and saves it in the repository.
     *
     * @param person the Person to create
     * @return the saved Person with an assigned ID
     */
    public Person createPerson(Person person) {
        return personRepository.save(person);
    }

    /**
     * Retrieves a Person by its ID.
     *
     * @param id the ID of the Person
     * @return an Optional containing the Person if found, otherwise empty
     */
    public Optional<Person> getPersonById(Integer id) {
        return personRepository.findById(id);
    }

    /**
     * Retrieves all Person entities.
     *
     * @return a list of all Persons
     */
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    /**
     * Updates an existing Person.
     * The Person must have a non-null ID.
     *
     * @param person the Person with updated details
     * @return the updated Person
     * @throws IllegalArgumentException if the Person ID is null
     */
    @Transactional
    public Person updatePerson(Person person) {
        if (person.getId() == null) {
            throw new IllegalArgumentException("Person ID must not be null for update.");
        }
        return personRepository.save(person);
    }

    /**
     * Deletes the Person with the specified ID.
     *
     * @param id the ID of the Person to delete
     */
    public void deletePerson(Integer id) {
        personRepository.deleteById(id);
    }
}
