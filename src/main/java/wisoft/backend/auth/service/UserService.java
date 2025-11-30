package wisoft.backend.auth.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wisoft.backend.auth.dto.request.DeleteUserRequest;
import wisoft.backend.auth.dto.request.RewardDeductRequest;
import wisoft.backend.auth.dto.request.UserProfileUpdateRequest;
import wisoft.backend.auth.dto.response.DeleteUserResponse;
import wisoft.backend.auth.dto.response.RewardDeductResponse;
import wisoft.backend.auth.dto.response.UserProfileResponse;
import wisoft.backend.auth.dto.response.UserProfileUpdateResponse;
import wisoft.backend.auth.entity.User;
import wisoft.backend.auth.repository.UserRepository;
import wisoft.backend.interviews.entity.History;
import wisoft.backend.interviews.repository.HistoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;

    public DeleteUserResponse deleteUser(String userId, DeleteUserRequest request) {
        User user = getUserById(userId);

        if ((user.getPassword().equals(request.password())) == false) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        List<History> histories = historyRepository.findByUser_IdOrderByCreatedAtDesc(userId);
        historyRepository.deleteAll(histories);
        userRepository.delete(user);

        return DeleteUserResponse.of(user.getName());
    }

    public RewardDeductResponse deductRewardPoints(String userId, @Valid RewardDeductRequest request) {
        User user = getUserById(userId);
        user.deductPoints(request.points());
        return RewardDeductResponse.of(request.points(), user.getPoints());
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(String userId) {
        User user = getUserById(userId);
        return UserProfileResponse.from(user);
    }

    public UserProfileUpdateResponse updateUserProfile(String userId, UserProfileUpdateRequest request) {
        User user = getUserById(userId);
        user.updateProfile(request.job(), request.scheduleType(), request.notificationType());
        return UserProfileUpdateResponse.from(user);
    }

    private User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }
}
