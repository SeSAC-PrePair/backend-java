package wisoft.backend.interviews.scheduler;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import wisoft.backend.auth.entity.ScheduleType;
import wisoft.backend.auth.entity.User;
import wisoft.backend.auth.repository.UserRepository;
import wisoft.backend.interviews.service.QuestionService;

@Component
@Slf4j
@RequiredArgsConstructor
public class QuestionScheduler {

    private final QuestionService questionService;
    private final UserRepository userRepository;

    /**
     * 매일 오전 9시에 일일 구독 사용자에게 질문 발송
     */
    @Scheduled(cron = "0 0 15 * * *", zone = "Asia/Seoul")
    public void sendDailyQuestions() {
        sendQuestionsByScheduleType(ScheduleType.DAILY);
    }

    /**
     * 매주 월요일 오전 9시에 주간 구독 사용자에게 질문 발송
     */
    @Scheduled(cron = "0 0 9 * * MON",  zone = "Asia/Seoul")
    public void sendWeeklyQuestions() {
        sendQuestionsByScheduleType(ScheduleType.WEEKLY);
    }

    /**
     * 스케줄 타입에 따라 질문 발송
     */
    private void sendQuestionsByScheduleType(ScheduleType scheduleType) {
        String typeName = scheduleType == ScheduleType.DAILY ? "일일" : "주간";
        log.info("[스케줄러] {} 질문 발송 시작", typeName);

        try {
            // 1. 스케줄 타입에 해당하는 사용자 조회
            List<User> users = userRepository.findBySchedule(scheduleType);

            if (users.isEmpty()) {
                log.info("[스케줄러] {} 구독 사용자가 없습니다.", typeName);
                return;
            }

            // 2. userId 리스트 추출
            List<String> userIds = users.stream()
                    .map(User::getId)
                    .collect(Collectors.toList());

            log.info("[스케줄러] {} 질문 발송 대상: {}명", typeName, userIds.size());

            // 3. 질문 생성 및 발송
            questionService.generateScheduledQuestions(userIds);

            log.info("[스케줄러] {} 질문 발송 완료 - 성공: {}명", typeName, userIds.size());

        } catch (Exception e) {
            log.error("[스케줄러] {} 질문 발송 중 오류 발생", typeName, e);
        }
    }
}