package wisoft.backend.interviews.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wisoft.backend.global.exception.ErrorCode;
import wisoft.backend.global.exception.custom.BusinessException;
import wisoft.backend.interviews.dto.response.QuestionDetailResponse;
import wisoft.backend.interviews.dto.response.QuestionHistoryResponse;
import wisoft.backend.interviews.dto.response.TodayQuestionResponse;
import wisoft.backend.interviews.entity.History;
import wisoft.backend.interviews.repository.HistoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterviewQueryService {

    private final HistoryRepository historyRepository;

    public TodayQuestionResponse getTodayQuestion(String userId) {
        History history = historyRepository.findTodayQuestionByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));

        return TodayQuestionResponse.from(history);
    }

    public List<QuestionHistoryResponse> getAllQuestion(String userId) {
        List<History> histories = historyRepository.findByUser_IdOrderByCreatedAtDesc(userId);

        return histories.stream()
                .map(QuestionHistoryResponse::from)
                .toList();
    }

    public QuestionDetailResponse getQuestionDetail(String historyId, String userId) {
        History history = historyRepository.findByHistoryIdAndUser_Id(historyId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));

        return QuestionDetailResponse.from(history);
    }
}
