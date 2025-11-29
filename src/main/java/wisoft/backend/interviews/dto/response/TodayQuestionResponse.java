package wisoft.backend.interviews.dto.response;

import wisoft.backend.interviews.entity.History;
import wisoft.backend.interviews.entity.QuestionStatus;

import java.time.LocalDateTime;

public record TodayQuestionResponse(
        String historyId,
        String questionId,
        String question,
        QuestionStatus status,
        LocalDateTime createdAt,
        LocalDateTime answeredAt
) {
    public static TodayQuestionResponse from(History history) {
        return new TodayQuestionResponse(
                history.getHistoryId(),
                history.getQuestionId(),
                history.getQuestion(),
                history.getStatus(),
                history.getCreatedAt(),
                history.getAnsweredAt()
        );
    }
}
