package wisoft.backend.auth.dto.response;

import lombok.Builder;
import wisoft.backend.auth.entity.NotificationType;
import wisoft.backend.auth.entity.ScheduleType;
import wisoft.backend.auth.entity.User;

@Builder
public record UserProfileResponse(
        String name,
        String email,
        String job,
        ScheduleType scheduleType,
        NotificationType notificationType
) {
    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .job(user.getJob())
                .scheduleType(user.getSchedule())
                .notificationType(user.getNotificationType())
                .build();
    }
}
