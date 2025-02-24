package info.mackiewicz.bankapp.controller.api;

import info.mackiewicz.bankapp.dto.UpdateUserRequest;
import info.mackiewicz.bankapp.dto.UserRegistrationDto;
import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.service.UserRegistrationService;
import info.mackiewicz.bankapp.service.UserService;
import info.mackiewicz.bankapp.validation.RequestValidator;
import info.mackiewicz.bankapp.mapper.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRegistrationService registrationService;
    private final RequestValidator requestValidator;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRegistrationDto registrationDto, BindingResult bindingResult) {
        ResponseEntity<?> validationError = requestValidator.validateRequest(bindingResult);
        if (validationError != null) {
            return validationError;
        }

        try {
            User created = registrationService.registerUser(registrationDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @Valid @RequestBody UpdateUserRequest updateRequest, BindingResult bindingResult) {
        ResponseEntity<?> validationError = requestValidator.validateRequest(bindingResult);
        if (validationError != null) {
            return validationError;
        }

        User existingUser = userService.getUserById(id);
        existingUser = userMapper.updateUserFromRequest(existingUser, updateRequest);
        
        
        return ResponseEntity.ok(userService.updateUser(existingUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
