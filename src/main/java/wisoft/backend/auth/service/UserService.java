package wisoft.backend.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wisoft.backend.auth.dto.DeleteUserRequest;
import wisoft.backend.auth.dto.DeleteUserResponse;
import wisoft.backend.auth.entity.User;
import wisoft.backend.auth.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public DeleteUserResponse deleteUser(String userId, DeleteUserRequest request) {

        Long id;
        try {
            id = Long.parseLong(userId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("올바르지 않은 사용자 ID 형식입니다.");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if ((user.getPassword().equals(request.password())) == false) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        userRepository.delete(user);

        return DeleteUserResponse.of(user.getName());
    }
}
