package wisoft.backend.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(name = "\"user\"")
public class User {

    @Id
    @Column(name = "user_id")
    private String id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String job;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleType schedule;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    @Column(nullable = false)
    @Builder.Default
    private Integer points = 0;

    /**
     * 포인트 차감
     */
    public void deductPoints(Integer points) {
        if (this.points < points) {
            throw new IllegalArgumentException("포인트가 부족합니다. (보유: " + this.points + ", 필요: " + points + ")");
        }
        this.points -= points;
    }

    /**
     * 프로필 업데이트 (job, schedule, notificationType만 변경 가능)
     */
    public void updateProfile(String job, ScheduleType scheduleType, NotificationType notificationType) {
        this.job = job;
        this.schedule = scheduleType;
        this.notificationType = notificationType;
    }
}
