package wisoft.backend.auth.dto.response;

import lombok.Builder;

@Builder
public record RewardDeductResponse(
        Integer usedPoints,
        Integer remainingPoints,
        String message
) {
    public static RewardDeductResponse of(Integer usedPoints, Integer remainingPoints) {
        return RewardDeductResponse.builder()
                .usedPoints(usedPoints)
                .remainingPoints(remainingPoints)
                .message("리워드 교환이 완료되었습니다.")
                .build();
    }
}
