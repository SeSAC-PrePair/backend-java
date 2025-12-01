package wisoft.backend.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
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
import wisoft.backend.auth.service.KakaoAuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final KakaoAuthService kakaoAuthService;

    @Value("${kakao.client.id}")
    private String clientId;

    @Value("${kakao.redirect.uri}")
    private String redirectUri;

    @Value("${frontend.url}")
    private String frontendUrl;

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

     /**
     * 카카오 OAuth 인증 페이지로 리다이렉트
     */
    @GetMapping("/kakao")
    public RedirectView authorize(@RequestParam("user_id") String userId) {
        String kakaoAuthUrl = String.format(
                "https://kauth.kakao.com/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code&scope=talk_message&state=%s",
                clientId,
                redirectUri,
                userId
        );
        return new RedirectView(kakaoAuthUrl);
    }

    /**
     * 카카오 OAuth 콜백 (authorization code 받음)
     */
    @GetMapping("/kakao/callback")
    public RedirectView callback(
            @RequestParam String code,
            @RequestParam String state) { // state = userId

        System.out.println("code: " + code);

        kakaoAuthService.handleCallback(code, state);

        // 프론트엔드 성공 페이지로 리다이렉트
        return new RedirectView(frontendUrl + "/signup-success?kakao=success&userId=" + state + "&needsKakaoAuth=true");
    }
}
