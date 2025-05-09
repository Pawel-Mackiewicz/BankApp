package info.mackiewicz.bankapp.presentation.auth.registration.controller;

import info.mackiewicz.bankapp.presentation.auth.registration.dto.demo.DemoRegistrationRequest;
import info.mackiewicz.bankapp.presentation.auth.registration.dto.RegistrationRequest;
import info.mackiewicz.bankapp.presentation.auth.registration.dto.RegistrationResponse;
import info.mackiewicz.bankapp.presentation.auth.registration.service.DemoRegistrationService;
import info.mackiewicz.bankapp.presentation.auth.registration.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DefaultRegistrationController  implements RegistrationController{

    private final RegistrationService registrationService;
    private final DemoRegistrationService demoRegistrationService;

    @Override
    public ResponseEntity<RegistrationResponse> registerUser(@Valid @RequestBody RegistrationRequest request) {

        RegistrationResponse response = registrationService.registerUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Override
    public ResponseEntity<RegistrationResponse> registerDemoUser(@Valid @RequestBody DemoRegistrationRequest request) {

        RegistrationResponse response = demoRegistrationService.registerDemoUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

}
