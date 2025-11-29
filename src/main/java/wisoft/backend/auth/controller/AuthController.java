package wisoft.backend.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wisoft.backend.auth.dto.request.EmailRequest;
import wisoft.backend.auth.dto.request.FindPasswordRequest;
import wisoft.backend.auth.dto.request.LoginRequest;
import wisoft.backend.auth.dto.request.SignupRequest;
import wisoft.backend.auth.dto.request.VerifyEmailRequest;
import wisoft.backend.auth.dto.response.EmailResponse;
import wisoft.backend.auth.dto.response.FindPasswordResponse;
import wisoft.backend.auth.dto.response.LoginResponse;
import wisoft.backend.auth.dto.response.SignupResponse;
import wisoft.backend.auth.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> signin(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.signin(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/password")
    public ResponseEntity<FindPasswordResponse> findPassword(@Valid @RequestBody FindPasswordRequest request) {
        FindPasswordResponse response = authService.findPassword(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/email/request")
    public ResponseEntity<EmailResponse> requestEmail(@Valid @RequestBody EmailRequest request) {
        EmailResponse response = authService.requestEmail(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/email/verify")
    public ResponseEntity<EmailResponse> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        EmailResponse response = authService.verifyEmail(request);
        return ResponseEntity.ok(response);
    }
}
