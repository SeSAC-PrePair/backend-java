package wisoft.backend.interviews.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wisoft.backend.ai.client.OpenRouterClient;
import wisoft.backend.ai.dto.AIPrompt;
import wisoft.backend.auth.entity.User;
import wisoft.backend.auth.repository.UserRepository;
import wisoft.backend.global.exception.ErrorCode;
import wisoft.backend.global.exception.custom.BusinessException;
import wisoft.backend.interviews.entity.History;
import wisoft.backend.interviews.entity.QuestionStatus;
import wisoft.backend.interviews.entity.InterviewQuestion;
import wisoft.backend.interviews.repository.HistoryRepository;
import wisoft.backend.interviews.repository.InterviewQuestionRepository;
import wisoft.backend.notification.service.NotificationService;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class QuestionService {

    private static final int SAMPLE_QUESTION_COUNT = 10;

    private final OpenRouterClient openRouterClient;
    private final HistoryRepository historyRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    /**
     * 새로운 면접 질문 생성 및 저장
     */
    public String generateQuestion(String userId) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 이전 질문 목록 조회 (중복 방지)
        List<String> previousQuestions = historyRepository
                .findByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(History::getQuestion)
                .collect(Collectors.toList());

        // 샘플 질문 10개 조회
        List<String> sampleQuestions = interviewQuestionRepository
                .findRandomQuestions(SAMPLE_QUESTION_COUNT)
                .stream()
                .map(InterviewQuestion::getQuestion)
                .toList();

        // AIPrompt 생성
        AIPrompt prompt = AIPrompt.of(
                user.getJob(),
                sampleQuestions,
                previousQuestions
        );

        // AI 호출
        String newQuestion = openRouterClient.generateQuestion(prompt);

        // History 저장
        History history = History.builder()
                .historyId("h_" + UUID.randomUUID())
                .user(user)
                .questionId("q_" + UUID.randomUUID())
                .question(newQuestion)
                .status(QuestionStatus.UNANSWERED)
                .build();

        historyRepository.save(history);

        // 알림 전송
        notificationService.sendQuestionNotification(userId, newQuestion);
        log.info("질문 생성 완료 - userId: {}, question: {}", userId, newQuestion);

        return newQuestion;
    }

    /**
     * 스케줄러용 - 여러 사용자에게 질문 생성
     */
    @Transactional
    public void generateScheduledQuestions(List<String> userIds) {
        userIds.forEach(userId -> {
            try {
                generateQuestion(userId);
                log.info("스케줄 질문 생성 완료 - userId: {}", userId);
            } catch (Exception e) {
                log.error("스케줄 질문 생성 실패 - userId: {}", userId, e);
            }
        });
    }
}