package wisoft.backend.auth.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wisoft.backend.auth.dto.request.DeleteUserRequest;
import wisoft.backend.auth.dto.request.RewardDeductRequest;
import wisoft.backend.auth.dto.response.DeleteUserResponse;
import wisoft.backend.auth.dto.response.RewardDeductResponse;
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

    @Transactional
    public RewardDeductResponse deductRewardPoints(String userId, @Valid RewardDeductRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        user.deductPoints(request.points());
        return RewardDeductResponse.of(request.points(), user.getPoints());
    }
}
