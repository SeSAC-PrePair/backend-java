package wisoft.backend.question.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuestionResponse {
    private String question;
    private String message;
}