package wisoft.backend.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wisoft.backend.auth.dto.request.DeleteUserRequest;
import wisoft.backend.auth.dto.response.DeleteUserResponse;
import wisoft.backend.auth.entity.User;
import wisoft.backend.auth.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public DeleteUserResponse deleteUser(String userId, DeleteUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if ((user.getPassword().equals(request.password())) == false) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        userRepository.delete(user);

        return DeleteUserResponse.of(user.getName());
    }
}
