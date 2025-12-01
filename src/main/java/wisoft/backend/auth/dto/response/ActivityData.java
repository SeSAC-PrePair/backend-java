package wisoft.backend.auth.dto.response;

import lombok.Builder;
import wisoft.backend.interviews.entity.History;

import java.time.LocalDateTime;

@Builder
public record ActivityData(
        String historyId,
        Integer score,
        LocalDateTime answeredAt
) {
    public static ActivityData from(History history) {
        return ActivityData.builder()
                .historyId(history.getHistoryId())
                .score(history.getScore())
                .answeredAt(history.getAnsweredAt())
                .build();
    }
}
