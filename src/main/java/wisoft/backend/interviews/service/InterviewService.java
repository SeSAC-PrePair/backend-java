package wisoft.backend.interviews.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wisoft.backend.interviews.dto.response.TodayQuestionResponse;
import wisoft.backend.interviews.entity.History;
import wisoft.backend.interviews.repository.HistoryRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterviewService {

    private final HistoryRepository historyRepository;

    public TodayQuestionResponse getTodayQuestion(String userId) {
        History history = historyRepository.findTodayQuestionByUserId(userId)
                .orElseThrow(() -> new RuntimeException("오늘 생성된 질문이 없습니다."));

        return TodayQuestionResponse.of(
                history.getHistoryId(),
                history.getQuestionId(),
                history.getQuestion(),
                history.getStatus(),
                history.getCreatedAt(),
                history.getAnsweredAt()
        );
    }
}
