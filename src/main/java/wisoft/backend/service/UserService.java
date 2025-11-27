package wisoft.backend.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wisoft.backend.dto.auth.DeleteUserRequest;
import wisoft.backend.dto.auth.DeleteUserResponse;
import wisoft.backend.entity.User;
import wisoft.backend.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public DeleteUserResponse deleteUser(String userId, @Valid DeleteUserRequest request) {
        Long id = Long.parseLong(userId);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if ((user.getPassword().equals(request.password())) == false) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        userRepository.delete(user);

        return DeleteUserResponse.of(user.getName());
    }
}
