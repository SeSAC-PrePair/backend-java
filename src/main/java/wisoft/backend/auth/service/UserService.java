package wisoft.backend.auth.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wisoft.backend.auth.dto.response.ActivityData;
import wisoft.backend.auth.dto.request.DeleteUserRequest;
import wisoft.backend.auth.dto.request.RewardDeductRequest;
import wisoft.backend.auth.dto.request.UserProfileUpdateRequest;
import wisoft.backend.auth.dto.response.*;
import wisoft.backend.auth.entity.User;
import wisoft.backend.auth.repository.UserRepository;
import wisoft.backend.exception.AuthenticationException;
import wisoft.backend.exception.ResourceNotFoundException;
import wisoft.backend.interviews.entity.History;
import wisoft.backend.interviews.entity.QuestionStatus;
import wisoft.backend.interviews.repository.HistoryRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;
    private final KakaoAuthService kakaoAuthService;

    public DeleteUserResponse deleteUser(String userId, DeleteUserRequest request) {
        User user = getUserById(userId);

        if ((user.getPassword().equals(request.password())) == false) {
            throw new AuthenticationException("비밀번호가 일치하지 않습니다.");
        }

        List<History> histories = historyRepository.findByUser_IdOrderByCreatedAtDesc(userId);
        historyRepository.deleteAll(histories);
        userRepository.delete(user);
        kakaoAuthService.removeTempToken(user.getEmail());

        return DeleteUserResponse.of(user.getName());
    }

    public RewardDeductResponse deductRewardPoints(String userId, @Valid RewardDeductRequest request) {
        User user = getUserById(userId);
        user.deductPoints(request.points());
        return RewardDeductResponse.of(request.points(), user.getPoints());
    }

    @Transactional(readOnly = true)
    public RewardPageResponse getRewardPage(String userId) {
        User user = getUserById(userId);
        return RewardPageResponse.of(user.getName(), user.getPoints());
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
                .orElseThrow(() -> new ResourceNotFoundException("사용자", userId));
    }

    @Transactional(readOnly = true)
    public UserSummaryResponse getUserSummary(String userId) {

        User user = getUserById(userId);

        // 답변한 질문 개수
        Integer answeredCount = historyRepository
                .countByUser_IdAndStatus(userId, QuestionStatus.ANSWERED);

        // 오늘의 점수
        Integer todayScore = historyRepository.findTodayQuestionByUserId(userId)
                .map(History::getScore)
                .orElse(null);

        // 보유 포인트
        Integer points = user.getPoints();

        // 답변한 질문들 조회
        List<History> answeredHistories = historyRepository
                .findByUser_IdAndStatus(userId, QuestionStatus.ANSWERED);

        // 연속 학습 일수 계산
        Integer consecutiveDays = calculateConsecutiveDays(answeredHistories);

        // 잔디 데이터
        List<ActivityData> activities = answeredHistories.stream()
                .map(ActivityData::from)
                .toList();

        return UserSummaryResponse.of(
                user.getName(),
                answeredCount,
                todayScore,
                points,
                consecutiveDays,
                activities
        );
    }

    /**
     * 연속 학습 일수 계산
     */
    private Integer calculateConsecutiveDays(List<History> histories) {
        if (histories.isEmpty()) {
            return 0;
        }

        Set<LocalDate> answeredDates = histories.stream()
                .map(History::getAnsweredAt)
                .filter(Objects::nonNull)
                .map(LocalDateTime::toLocalDate)
                .collect(Collectors.toSet());

        LocalDate today = LocalDate.now();
        int consecutiveDays = 0;

        LocalDate checkDate = today;

        while (answeredDates.contains(checkDate)) {
            consecutiveDays++;
            checkDate = checkDate.minusDays(1);
        }
        return consecutiveDays;
    }
}
