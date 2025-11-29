package wisoft.backend.interviews.dto.response;

import wisoft.backend.interviews.entity.History;
import wisoft.backend.interviews.entity.QuestionStatus;

import java.time.LocalDateTime;

public record QuestionHistoryResponse(
        String historyId,
        String questionId,
        String question,
        Integer score,
        QuestionStatus status,
        LocalDateTime createdAt,
        LocalDateTime answeredAt
) {
    public static QuestionHistoryResponse from(History history) {
        return new QuestionHistoryResponse(
                history.getHistoryId(),
                history.getQuestionId(),
                history.getQuestion(),
                history.getScore(),
                history.getStatus(),
                history.getCreatedAt(),
                history.getAnsweredAt()
        );
    }
}
