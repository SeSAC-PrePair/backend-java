package wisoft.backend.auth.dto.response;

import lombok.Builder;
import wisoft.backend.auth.entity.NotificationType;
import wisoft.backend.auth.entity.ScheduleType;
import wisoft.backend.auth.entity.User;

@Builder
public record UserProfileUpdateResponse(
        String job,
        ScheduleType scheduleType,
        NotificationType notificationType,
        String message
) {
    public static UserProfileUpdateResponse from(User user) {
        return UserProfileUpdateResponse.builder()
                .job(user.getJob())
                .scheduleType(user.getSchedule())
                .notificationType(user.getNotificationType())
                .message("프로필이 성공적으로 수정되었습니다.")
                .build();
    }
}
