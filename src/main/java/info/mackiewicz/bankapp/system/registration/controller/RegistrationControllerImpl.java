package info.mackiewicz.bankapp.system.registration.controller;

import info.mackiewicz.bankapp.presentation.auth.dto.UserRegistrationDto;
import info.mackiewicz.bankapp.presentation.auth.service.UserRegistrationService;
import info.mackiewicz.bankapp.system.registration.dto.RegistrationResponse;
import info.mackiewicz.bankapp.user.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RegistrationControllerImpl {

    private final UserRegistrationService registrationService;

    public ResponseEntity<RegistrationResponse> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {

        User created = registrationService.registerUser(registrationDto);


        throw new UnsupportedOperationException("Not implemented yet");
    }

}
