package wisoft.backend.auth.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record UserSummaryResponse(
        String name,
        Integer answeredQuestionCount,
        Integer todayScore,
        Integer points,
        Integer consecutiveDays,
        List<ActivityData> activities
) {
    public static UserSummaryResponse of(
            String name,
            Integer answeredQuestionCount,
            Integer todayScore,
            Integer points,
            Integer consecutiveDays,
            List<ActivityData> activities
    ) {
        return UserSummaryResponse.builder()
                .name(name)
                .answeredQuestionCount(answeredQuestionCount)
                .todayScore(todayScore)
                .points(points)
                .consecutiveDays(consecutiveDays)
                .activities(activities)
                .build();
    }
}
