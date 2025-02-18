package info.mackiewicz.bankapp.controller.api;

import info.mackiewicz.bankapp.dto.UserRegistrationDto;
import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.service.UserRegistrationService;
import info.mackiewicz.bankapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

//BY CHATGPT
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRegistrationService registrationService;

    // Constructor injection for UserService
    public UserController(UserService userService,
                          UserRegistrationService registrationService) {
        this.userService = userService;
        this.registrationService = registrationService;
    }

    // Endpoint rejestracji użytkownika przyjmujący DTO
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRegistrationDto registrationDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            User created = registrationService.registerUser(registrationDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // Retrieve a User by ID
    // GET /api/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
            User userOpt = userService.getUserById(id);
            return ResponseEntity.ok(userOpt);
    }
    // Retrieve all Users
    // GET /api/users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    // Update an existing User
    // PUT /api/users/{id}
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User user) {
        // Ensure the User has the correct ID
            user.setId(id);
            User updated = userService.updateUser(user);
            return ResponseEntity
                    .ok(updated);
    }

    // Delete a User by ID
    // DELETE /api/users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
            userService.deleteUser(id);
            return ResponseEntity
                    .noContent()
                    .build();
    }
}
