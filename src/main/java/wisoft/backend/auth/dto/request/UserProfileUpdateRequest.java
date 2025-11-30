package wisoft.backend.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import wisoft.backend.auth.entity.NotificationType;
import wisoft.backend.auth.entity.ScheduleType;

public record UserProfileUpdateRequest(
        @NotBlank(message = "직업은 필수입니다.")
        String job,

        @NotNull(message = "알림 방법은 필수입니다.")
        ScheduleType scheduleType,

        @NotNull(message = "알림 방법은 필수입니다.")
        NotificationType notificationType
) {

}
