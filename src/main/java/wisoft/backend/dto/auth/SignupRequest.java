package wisoft.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignupRequest(
        @NotBlank
        @Email(message = "유효한 이메일 형식이어야 합니다.")
        String email,

        @NotBlank
        @Pattern(
                regexp = "^(?=.*[!@#$%^&*])(.{6,})$",
                message = "비밀번호는 특수문자 1개 이상, 6자 이상이어야 합니다."
        )
        String password,

        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotBlank(message = "직업은 필수입니다.")
        String job,

        @NotBlank(message = "질문 주기 설정은 필수입니다")
        @Pattern(
                regexp = "DAILY|WEEKLY",
                message = "질문 주기는 DAILY 또는 WEEKLY만 가능합니다."
        )
        String schedule,

        @NotBlank(message = "알림 타입은 필수입니다.")
        @Pattern(
                regexp = "EMAIL|KAKAO|BOTH",
                message = "알림 타입은 EMAIL, KAKAO, BOTH 중 하나여야 합니다."
        )
        String notificationType
) {
}
