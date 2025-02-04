package info.mackiewicz.bankapp.repository;

import info.mackiewicz.bankapp.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Integer> {
}
