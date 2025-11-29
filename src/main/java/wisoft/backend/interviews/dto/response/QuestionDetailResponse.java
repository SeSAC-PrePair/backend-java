package wisoft.backend.interviews.dto.response;

import wisoft.backend.interviews.entity.History;
import wisoft.backend.interviews.entity.QuestionStatus;

import java.time.LocalDateTime;

public record QuestionDetailResponse(
        String historyId,
        String questionId,
        String question,
        String answer,
        String feedback,
        Integer score,
        QuestionStatus status,
        LocalDateTime createdAt,
        LocalDateTime answeredAt
) {
    public static QuestionDetailResponse from(History history) {
        return new QuestionDetailResponse(
                history.getHistoryId(),
                history.getQuestionId(),
                history.getQuestion(),
                history.getAnswer(),
                history.getFeedback(),
                history.getScore(),
                history.getStatus(),
                history.getCreatedAt(),
                history.getAnsweredAt()
        );
    }
}
