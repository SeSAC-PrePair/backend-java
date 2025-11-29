package wisoft.backend.interviews.dto.response;

import wisoft.backend.interviews.entity.QuestionStatus;

import java.time.LocalDateTime;

public record TodayQuestionResponse(
        String historyId,
        String questionId,
        String question,
        QuestionStatus status,
        LocalDateTime createdAt,
        LocalDateTime answeredAt,
        String message
) {
    public static TodayQuestionResponse of(
            String historyId,
            String questionId,
            String question,
            QuestionStatus status,
            LocalDateTime createdAt,
            LocalDateTime answeredAt) {
        return new TodayQuestionResponse(
                historyId,
                questionId,
                question,
                status,
                createdAt,
                answeredAt,
                "오늘의 질문을 조회했습니다.");
    }

}
