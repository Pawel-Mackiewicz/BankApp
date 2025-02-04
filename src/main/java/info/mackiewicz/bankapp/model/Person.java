package info.mackiewicz.bankapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Collection;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "People")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String lastname;
    private LocalDate dateOfBirth;
    private String PESEL;

    @OneToMany(mappedBy = "owner")
    private Collection<Account> accounts;

}

