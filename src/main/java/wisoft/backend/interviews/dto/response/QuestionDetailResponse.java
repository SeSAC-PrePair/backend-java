package wisoft.backend.interviews.dto.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import wisoft.backend.interviews.entity.History;
import wisoft.backend.interviews.entity.QuestionStatus;

import java.time.LocalDateTime;

public record QuestionDetailResponse(
        String historyId,
        String questionId,
        String question,
        String answer,
        Integer score,
        QuestionStatus status,
        LocalDateTime createdAt,
        LocalDateTime answeredAt,
        FeedbackDetail feedback
) {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static QuestionDetailResponse from(History history) {
        FeedbackDetail feedbackDetail = null;
        String feedbackJson = history.getFeedback();

        if (feedbackJson != null && !feedbackJson.isBlank()) {
            try {
                feedbackDetail = objectMapper.readValue(feedbackJson, FeedbackDetail.class);
            } catch (Exception e) {
                throw new RuntimeException("feedback JSON parsing failed", e);
            }
        }

        return new QuestionDetailResponse(
                history.getHistoryId(),
                history.getQuestionId(),
                history.getQuestion(),
                history.getAnswer(),
                history.getScore(),
                history.getStatus(),
                history.getCreatedAt(),
                history.getAnsweredAt(),
                feedbackDetail
        );
    }
}
