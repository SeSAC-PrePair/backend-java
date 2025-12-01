package wisoft.backend.interviews.dto.response;

import lombok.Builder;
import wisoft.backend.interviews.entity.History;

@Builder
public record FeedbackDetail(
        String good,
        String improvement,
        String recommendation
) {
}
