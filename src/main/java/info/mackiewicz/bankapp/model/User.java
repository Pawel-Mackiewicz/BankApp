package info.mackiewicz.bankapp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.Collection;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String lastname;
    private LocalDate dateOfBirth;
    @Column(unique = true)
    private String PESEL;

    @JsonBackReference
    @OneToMany(mappedBy = "owner")
    private Collection<Account> accounts;

}

