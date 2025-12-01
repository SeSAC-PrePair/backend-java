package wisoft.backend.auth.dto.response;

import lombok.Builder;

@Builder
public record RewardPageResponse(
        String name,
        Integer points
) {
    public static RewardPageResponse of(String name, Integer points) {
        return RewardPageResponse.builder()
                .name(name)
                .points(points)
                .build();
    }
}
