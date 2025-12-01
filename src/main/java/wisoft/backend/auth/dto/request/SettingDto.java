package wisoft.backend.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import wisoft.backend.auth.entity.NotificationType;
import wisoft.backend.auth.entity.ScheduleType;

public record SettingDto(
        @NotBlank(message = "직업은 필수입니다.")
        String job,

        @NotNull(message = "질문 빈도는 필수입니다.")
        ScheduleType scheduleType,

        @NotNull(message = "알림 타입은 필수입니다.")
        NotificationType notificationType
) {
}
