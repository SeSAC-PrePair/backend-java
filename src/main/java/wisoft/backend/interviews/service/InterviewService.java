package wisoft.backend.interviews.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wisoft.backend.interviews.dto.response.QuestionDetailResponse;
import wisoft.backend.interviews.dto.response.QuestionHistoryResponse;
import wisoft.backend.interviews.dto.response.TodayQuestionResponse;
import wisoft.backend.interviews.entity.History;
import wisoft.backend.interviews.repository.HistoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterviewService {

    private final HistoryRepository historyRepository;

    public TodayQuestionResponse getTodayQuestion(String userId) {
        History history = historyRepository.findTodayQuestionByUserId(userId)
                .orElseThrow(() -> new RuntimeException("오늘 생성된 질문이 없습니다."));

        return TodayQuestionResponse.from(history);
    }

    public List<QuestionHistoryResponse> getAllQuestion(String userId) {
        List<History> histories = historyRepository.findByUser_IdOrderByCreatedAtDesc(userId);

        return histories.stream()
                .map(QuestionHistoryResponse::from)
                .toList();
    }

    public QuestionDetailResponse getQuestionDetail(String historyId, String userId) {
        History history = historyRepository.findByHistoryIdAndUser_Id(historyId, userId).orElseThrow(
                () -> new RuntimeException("질문을 찾을 수 없습니다."));

        return QuestionDetailResponse.from(history);
    }

}
