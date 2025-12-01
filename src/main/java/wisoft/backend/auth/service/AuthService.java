package wisoft.backend.auth.service;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wisoft.backend.auth.dto.request.EmailRequest;
import wisoft.backend.auth.dto.request.FindPasswordRequest;
import wisoft.backend.auth.dto.request.LoginRequest;
import wisoft.backend.auth.dto.request.SignupRequest;
import wisoft.backend.auth.dto.request.VerifyEmailRequest;
import wisoft.backend.auth.dto.response.EmailResponse;
import wisoft.backend.auth.dto.response.FindPasswordResponse;
import wisoft.backend.auth.dto.response.LoginResponse;
import wisoft.backend.auth.dto.response.SignupResponse;
import wisoft.backend.auth.entity.User;
import wisoft.backend.auth.repository.UserRepository;
import wisoft.backend.exception.AuthenticationException;
import wisoft.backend.exception.BusinessException;
import wisoft.backend.exception.ConflictException;
import wisoft.backend.exception.ResourceNotFoundException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final KakaoAuthService kakaoAuthService;
    private final Map<String, VerificationData> verificationCodes = new ConcurrentHashMap<>();
    private final Set<String> verifiedEmails = ConcurrentHashMap.newKeySet();

    @Transactional
    public SignupResponse signup(SignupRequest request) {

        try {
            if (userRepository.existsByEmail(request.email()) == true) {
                throw new ConflictException("이미 사용 중인 이메일입니다.");
            }

            if (verifiedEmails.contains(request.email()) == false) {
                throw new BusinessException("EMAIL_VERIFICATION_REQUIRED", "이메일 인증이 필요합니다.");
            }

            String id = "u_" + UUID.randomUUID();
            User user = User.builder()
                    .id(id)
                    .email(request.email())
                    .password(request.password())
                    .name(request.name())
                    .job(request.settings().job())
                    .schedule(request.settings().scheduleType())
                    .notificationType(request.settings().notificationType())
                    .build();

            User savedUser = userRepository.save(user);
            verifiedEmails.remove(request.email());

            // 임시 저장된 카카오 토큰 연결
            kakaoAuthService.linkTempTokenToUser(request.email(), savedUser);

            return SignupResponse.of(
                    savedUser.getId(),
                    savedUser.getEmail(),
                    savedUser.getName()
            );
        } catch (Exception e) {
            kakaoAuthService.removeTempToken(request.email());
            throw e;
        }
    }

    public LoginResponse signin(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("이메일", request.email()));

        if ((user.getPassword().equals(request.password())) == false) {
            throw new AuthenticationException("비밀번호가 일치하지 않습니다.");
        }

        return LoginResponse.of(
                user.getId(),
                user.getEmail(),
                user.getName());
    }

    public FindPasswordResponse findPassword(FindPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("이메일", request.email()));

        return FindPasswordResponse.of(
                user.getEmail(),
                user.getPassword());
    }

    public EmailResponse requestEmail(EmailRequest request) {
        String code = generateRandomCode();
        verificationCodes.put(request.email(), new VerificationData(code, LocalDateTime.now().plusMinutes(5)));
        emailService.sendVerificationEmail(request.email(), code);

        return EmailResponse.of("인증 코드가 이메일로 전송되었습니다.");
    }

    private String generateRandomCode() {
        SecureRandom random = new SecureRandom();
        int code = random.nextInt(1000000);
        return String.format("%06d", code);
    }

    public EmailResponse verifyEmail(VerifyEmailRequest request) {
        VerificationData data = verificationCodes.get(request.email());

        if (data == null) {
            throw new BusinessException("VERIFICATION_CODE_NOT_FOUND", "인증 코드가 존재하지 않습니다.");
        }

        if (data.isExpired()) {
            verificationCodes.remove(request.email());
            throw new BusinessException("VERIFICATION_CODE_EXPIRED", "인증 코드가 만료되었습니다.");
        }

        if (data.code.equals(request.code()) == false) {
            throw new BusinessException("VERIFICATION_CODE_MISMATCH", "인증 코드가 일치하지 않습니다.");
        }

        verificationCodes.remove(request.email());
        verifiedEmails.add(request.email());
        return EmailResponse.of("이메일이 인증되었습니다.");
    }

    private static class VerificationData {
        private final String code;
        private final LocalDateTime expiryTime;

        public VerificationData(String code, LocalDateTime expiryTime) {
            this.code = code;
            this.expiryTime = expiryTime;
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiryTime);
        }
    }
}
