package wisoft.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wisoft.backend.dto.auth.SignupRequest;
import wisoft.backend.dto.auth.SignupResponse;
import wisoft.backend.entity.User;
import wisoft.backend.repository.UserRepository;

@Service
@RequiredArgsConstructor

public class AuthService {

    private final UserRepository userRepository;

    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email()) == true) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User user = User.builder()
                .email(request.email())
                .password(request.password())
                .name(request.name())
                .job(request.job())
                .schedule(request.schedule())
                .notificationType(request.notificationType())
                .build();

        User savedUser = userRepository.save(user);

        return SignupResponse.of(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName()
        );
    }
}
