package wisoft.backend.auth.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RewardDeductRequest(
        @NotNull(message = "차감할 포인트는 필수입니다.")
        @Min(value = 1, message = "포인트는 1 이상이어야 합니다.")
        Integer points
) {
}
