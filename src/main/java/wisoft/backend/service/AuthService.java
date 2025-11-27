package wisoft.backend.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wisoft.backend.dto.auth.*;
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
                .job(convertToJob(request))
                .schedule(request.settings().questionFrequency())
                .notificationType(request.settings().notification())
                .build();

        User savedUser = userRepository.save(user);

        return SignupResponse.of(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName()
        );
    }

    private String convertToJob(SignupRequest request) {
        return request.settings().jobCategory() + "_" + request.settings().jobRole();
    }

    public LoginResponse signin(@Valid LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if ((user.getPassword().equals(request.password())) == false) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return LoginResponse.of(
                user.getId(),
                user.getEmail(),
                user.getName());
    }

    public FindPasswordResponse findPassword(@Valid FindPasswordRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        return FindPasswordResponse.of(
                user.getEmail(),
                user.getPassword());
    }
}
