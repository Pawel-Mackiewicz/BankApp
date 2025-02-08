package info.mackiewicz.bankapp.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Collection;

@NoArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true)
    private String PESEL;
    private String name;
    private String lastname;
    private LocalDate dateOfBirth;
    private String email;
    private String password;

    @JsonManagedReference
    @OneToMany(mappedBy = "owner")
    private Collection<Account> accounts;

}

