package info.mackiewicz.bankapp.controller;

import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

//BY CHATGPT
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Constructor injection for UserService
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Create a new User
    // POST /api/users
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User created = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
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
