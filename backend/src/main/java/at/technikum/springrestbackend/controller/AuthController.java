package at.technikum.springrestbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import at.technikum.springrestbackend.dto.login.LoginRequest;
import at.technikum.springrestbackend.dto.login.TokenResponse;
import at.technikum.springrestbackend.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final AuthService authService;

    /**
     * Handles user login requests.
     *
     * @param loginRequest the login request containing the username and password
     * @return a TokenResponse containing the authentication token if the login is successful
     * @throws IllegalArgumentException if the login request is invalid
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest loginRequest) {

        try {
            TokenResponse tokenResponse = authService.attemptLogin(loginRequest);
            return ResponseEntity.ok(tokenResponse);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

    }
}
